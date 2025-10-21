"""HTTP routes exposed by the AI agent backend."""

from typing import List

from fastapi import APIRouter, Depends, status

from ..dependencies import get_data_service, get_llm_service
from ..models.chat import ChatRequest, ChatResponse, ContextResult
from ..services.data_service import DataService
from ..services.llm_service import LLMService

router = APIRouter()


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

