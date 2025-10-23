"""Service responsible for orchestrating chat completions with OpenAI."""

from __future__ import annotations

import asyncio
import json
from typing import Any, AsyncIterator, Dict, List, Optional

from fastapi import HTTPException
from openai import OpenAIError

from ..clients.openai_client import OpenAIChatClient
from ..models.chat import ChatRequest, ChatResponse, ContextResult, Message
from .knowledge_service import KnowledgeService


class LLMService:
    """Coordinates prompt assembly, enterprise lookups, and knowledge search."""

    def __init__(
        self,
        client: OpenAIChatClient,
        default_model: str,
        timeout_seconds: int,
        knowledge_service: Optional[KnowledgeService] = None,
    ) -> None:
        self._client = client
        self._default_model = default_model
        self._timeout_seconds = timeout_seconds
        self._knowledge_service = knowledge_service

    async def generate_reply(self, request: ChatRequest, context_results: List[ContextResult]) -> ChatResponse:
        """Return an assistant reply for the supplied chat request."""
        if not request.messages:
            raise HTTPException(status_code=400, detail="At least one message is required to generate a reply.")

        knowledge_context = await self._retrieve_knowledge(request)
        combined_contexts = [*context_results, *knowledge_context]

        serialized_messages = [message.model_dump() for message in request.messages]
        if combined_contexts:
            serialized_messages.append(self._create_context_message(combined_contexts))

        try:
            completion = await self._client.create_chat_completion(
                messages=serialized_messages,
                model=self._default_model,
                timeout_seconds=self._timeout_seconds,
            )
        except OpenAIError as exc:  # pragma: no cover - requires real OpenAI calls
            raise HTTPException(status_code=502, detail=f"OpenAI error: {exc}") from exc

        message = completion["message"]
        content = message.get("content", "")
        if isinstance(content, list):
            content = " ".join(
                part.get("text", "")
                for part in content
                if isinstance(part, dict) and part.get("type") == "text"
            )

        reply = Message(role=message.get("role", "assistant"), content=content)
        usage = completion.get("usage")
        raw_response = completion.get("raw")

        return ChatResponse(
            conversation_id=request.conversation_id,
            reply=reply,
            context=combined_contexts,
            usage=usage,
            raw_response=raw_response,
        )

    async def stream_reply(
        self, request: ChatRequest, context_results: List[ContextResult]
    ) -> AsyncIterator[Dict[str, Any]]:
        """Yield streaming events for the supplied chat request."""
        if not request.messages:
            raise HTTPException(status_code=400, detail="At least one message is required to generate a reply.")

        knowledge_context = await self._retrieve_knowledge(request)
        combined_contexts = [*context_results, *knowledge_context]

        serialized_messages = [message.model_dump() for message in request.messages]
        if combined_contexts:
            serialized_messages.append(self._create_context_message(combined_contexts))

        try:
            async for event in self._client.stream_chat_completion(
                messages=serialized_messages,
                model=self._default_model,
                timeout_seconds=self._timeout_seconds,
            ):
                if event.get("type") == "final":
                    yield self._build_final_event(request, combined_contexts, event)
                else:
                    yield event
        except OpenAIError as exc:  # pragma: no cover - requires real OpenAI calls
            raise HTTPException(status_code=502, detail=f"OpenAI error: {exc}") from exc

    def _build_final_event(
        self,
        request: ChatRequest,
        context_results: List[ContextResult],
        event: Dict[str, Any],
    ) -> Dict[str, Any]:
        """Construct the final streaming payload that mirrors the standard response."""
        message_payload = event.get("message", {})
        content = message_payload.get("content", "")
        if isinstance(content, list):
            content = " ".join(
                part.get("text", "")
                for part in content
                if isinstance(part, dict) and part.get("type") == "text"
            )

        reply = Message(role=message_payload.get("role", "assistant"), content=content)

        final_event: Dict[str, Any] = {
            "type": "final",
            "conversation_id": request.conversation_id,
            "reply": reply.model_dump(),
            "context": [context.model_dump() for context in context_results],
            "usage": event.get("usage"),
            "raw_response": event.get("raw"),
            "finish_reason": event.get("finish_reason"),
        }

        return final_event

    async def _retrieve_knowledge(self, request: ChatRequest) -> List[ContextResult]:
        """Search ChromaDB for content that can enrich the prompt."""
        if not self._knowledge_service or not self._knowledge_service.enabled:
            return []
        query = self._extract_latest_user_message(request)
        if not query:
            return []
        return await asyncio.to_thread(self._knowledge_service.search, query)

    @staticmethod
    def _extract_latest_user_message(request: ChatRequest) -> str:
        """Return the most recent user message content."""
        for message in reversed(request.messages):
            if message.role == "user" and message.content:
                return message.content.strip()
        return ""

    def _create_context_message(self, context_results: List[ContextResult]) -> dict:
        """Format context payloads into a system message consumable by the LLM."""
        context_payload = {context.name: context.data for context in context_results}
        summary = json.dumps(context_payload, ensure_ascii=False, indent=2)
        return {
            "role": "system",
            "content": "Enterprise context data:\n" + summary,
        }
