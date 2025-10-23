"""HTTP routes exposed by the AI agent backend."""

import json
import logging
from typing import List

from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.responses import StreamingResponse

from ..dependencies import get_data_service, get_llm_service
from ..models.chat import ChatRequest, ChatResponse, ContextResult
from ..services.data_service import DataService
from ..services.llm_service import LLMService

router = APIRouter()
logger = logging.getLogger(__name__)


@router.get("/health", tags=["system"], status_code=status.HTTP_200_OK)
async def health_check() -> dict[str, str]:
    """Return a simple health payload for monitoring."""
    return {"status": "ok"}


@router.post("/chat", response_model=ChatResponse, tags=["chat"], status_code=status.HTTP_200_OK)
async def chat_with_agent(
    payload: ChatRequest,
    llm_service: LLMService = Depends(get_llm_service),
    data_service: DataService = Depends(get_data_service),
) -> ChatResponse:
    """Generate an assistant reply using OpenAI along with optional enterprise data."""
    context_results: List[ContextResult] = []
    if payload.context_requests:
        context_results = await data_service.fetch_context(payload.context_requests)

    return await llm_service.generate_reply(payload, context_results)


@router.post(
    "/chat/stream",
    tags=["chat"],
    status_code=status.HTTP_200_OK,
    response_description="Server-Sent Events stream of assistant deltas and final payload.",
)
async def chat_with_agent_stream(
    payload: ChatRequest,
    llm_service: LLMService = Depends(get_llm_service),
    data_service: DataService = Depends(get_data_service),
) -> StreamingResponse:
    """Stream assistant tokens to the client as they are produced."""
    if not payload.messages:
        raise HTTPException(status_code=400, detail="At least one message is required to generate a reply.")

    context_results: List[ContextResult] = []
    if payload.context_requests:
        context_results = await data_service.fetch_context(payload.context_requests)

    async def event_generator():
        done_sent = False
        try:
            async for event in llm_service.stream_reply(payload, context_results):
                yield f"data: {json.dumps(event, ensure_ascii=False)}\n\n"
            done_sent = True
            yield "data: [DONE]\n\n"
        except HTTPException as exc:
            error_event = {
                "type": "error",
                "status": exc.status_code,
                "detail": exc.detail if isinstance(exc.detail, str) else str(exc.detail),
            }
            yield f"data: {json.dumps(error_event, ensure_ascii=False)}\n\n"
        except Exception as exc:  # pragma: no cover - defensive guard
            logger.exception("Unhandled error while streaming chat response")
            error_event = {
                "type": "error",
                "status": 500,
                "detail": "AI 助手暫時無法回應，請稍後再試。",
            }
            yield f"data: {json.dumps(error_event, ensure_ascii=False)}\n\n"
        finally:
            if not done_sent:
                yield "data: [DONE]\n\n"

    headers = {
        "Cache-Control": "no-cache",
        "Connection": "keep-alive",
        "X-Accel-Buffering": "no",
    }

    return StreamingResponse(event_generator(), media_type="text/event-stream", headers=headers)
