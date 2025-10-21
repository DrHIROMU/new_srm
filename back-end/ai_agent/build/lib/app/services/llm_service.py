"""Service responsible for orchestrating chat completions with OpenAI."""

from __future__ import annotations

import json
from typing import List

from ..clients.openai_client import OpenAIChatClient
from ..models.chat import ChatRequest, ChatResponse, ContextResult, Message


class LLMService:
    """Coordinates prompt assembly and OpenAI chat completion calls."""

    def __init__(self, client: OpenAIChatClient, default_model: str, timeout_seconds: int) -> None:
        self._client = client
        self._default_model = default_model
        self._timeout_seconds = timeout_seconds

    async def generate_reply(self, request: ChatRequest, context_results: List[ContextResult]) -> ChatResponse:
        """Return an assistant reply for the supplied chat request."""
        if not request.messages:
            raise ValueError("At least one message is required to generate a reply.")

        serialized_messages = [message.model_dump() for message in request.messages]
        if context_results:
            serialized_messages.append(self._create_context_message(context_results))

        completion = await self._client.create_chat_completion(
            messages=serialized_messages,
            model=self._default_model,
            timeout_seconds=self._timeout_seconds,
        )

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
            context=context_results,
            usage=usage,
            raw_response=raw_response,
        )

    def _create_context_message(self, context_results: List[ContextResult]) -> dict:
        """Format context payloads into a system message consumable by the LLM."""
        context_payload = {context.name: context.data for context in context_results}
        summary = json.dumps(context_payload, ensure_ascii=False, indent=2)
        return {
            "role": "system",
            "content": "Enterprise context data:\n" + summary,
        }
