import json

import pytest
from fastapi.testclient import TestClient

from app.dependencies import get_data_service, get_llm_service
from app.main import create_application
from app.models.chat import ChatRequest, ChatResponse, ContextResult, Message


class StubLLMService:
    async def generate_reply(self, request: ChatRequest, context_results: list[ContextResult]) -> ChatResponse:
        return ChatResponse(
            conversation_id=request.conversation_id,
            reply=Message(role="assistant", content="stubbed response"),
            context=context_results,
            usage={"prompt_tokens": 0, "completion_tokens": 0, "total_tokens": 0},
            raw_response={"provider": "stub"},
        )

    async def stream_reply(self, request: ChatRequest, context_results: list[ContextResult]):
        yield {"type": "content.delta", "delta": "stubbed "}
        yield {"type": "content.delta", "delta": "response"}
        yield {"type": "content.done", "content": "stubbed response"}
        yield {
            "type": "final",
            "conversation_id": request.conversation_id,
            "reply": Message(role="assistant", content="stubbed response").model_dump(),
            "context": [result.model_dump() for result in context_results],
            "usage": {"prompt_tokens": 0, "completion_tokens": 0, "total_tokens": 0},
            "raw_response": {"provider": "stub"},
            "finish_reason": "stop",
        }


class StubDataService:
    async def fetch_context(self, context_requests):
        return [ContextResult(name=req.name, data={"status": "ok"}) for req in context_requests or []]


@pytest.fixture()
def client():
    app = create_application()
    app.dependency_overrides[get_llm_service] = lambda: StubLLMService()
    app.dependency_overrides[get_data_service] = lambda: StubDataService()
    yield TestClient(app)
    app.dependency_overrides.clear()


def test_health_check(client: TestClient):
    response = client.get("/api/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_chat_endpoint_returns_stubbed_payload(client: TestClient):
    payload = {
        "messages": [{"role": "user", "content": "hello"}],
        "context_requests": [{"name": "vendors", "endpoint": "/vendors"}],
    }

    response = client.post("/api/chat", json=payload)

    assert response.status_code == 200
    body = response.json()
    assert body["reply"]["content"] == "stubbed response"
    assert body["context"][0]["name"] == "vendors"


def test_chat_stream_endpoint_streams_stubbed_events(client: TestClient):
    payload = {
        "conversation_id": "conv-1",
        "messages": [{"role": "user", "content": "hello"}],
        "context_requests": [{"name": "vendors", "endpoint": "/vendors"}],
    }

    with client.stream("POST", "/api/chat/stream", json=payload) as response:
        assert response.status_code == 200
        data_lines: list[str] = []
        for chunk in response.iter_text():
            for line in chunk.splitlines():
                if line.startswith("data: "):
                    data_lines.append(line.removeprefix("data: ").strip())

    assert data_lines[-1] == "[DONE]"
    events = [json.loads(item) for item in data_lines[:-1]]
    deltas = [event["delta"] for event in events if event.get("type") == "content.delta"]
    assert deltas == ["stubbed ", "response"]
    assert {"type": "content.done", "content": "stubbed response"} in events
    final_event = next(event for event in events if event.get("type") == "final")
    assert final_event["type"] == "final"
    assert final_event["conversation_id"] == "conv-1"
    assert final_event["reply"]["content"] == "stubbed response"
    assert final_event["context"][0]["name"] == "vendors"
