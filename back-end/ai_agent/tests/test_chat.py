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
