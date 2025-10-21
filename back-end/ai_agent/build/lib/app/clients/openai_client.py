"""Wrapper around the OpenAI SDK to simplify interactions."""

from __future__ import annotations

from typing import Any, Dict, List, Optional, TYPE_CHECKING

try:
    from openai import AsyncOpenAI
except ImportError as exc:  # pragma: no cover - handled at runtime
    AsyncOpenAI = None  # type: ignore[assignment]
    _IMPORT_ERROR = exc
else:
    _IMPORT_ERROR = None

if TYPE_CHECKING:  # pragma: no cover
    from openai.types.chat import ChatCompletion
else:  # pragma: no cover - for runtime without type checking
    ChatCompletion = Any


class OpenAIChatClient:
    """Thin abstraction over OpenAI's Chat Completions API."""

    def __init__(self, api_key: Optional[str]) -> None:
        if not api_key:
            raise ValueError("OPENAI_API_KEY is not configured.")
        if AsyncOpenAI is None:
            message = "openai dependency not available. Install the requirements first."
            raise RuntimeError(message) from _IMPORT_ERROR

        self._client = AsyncOpenAI(api_key=api_key)

    async def create_chat_completion(
        self,
        *,
        messages: List[Dict[str, Any]],
        model: str,
        timeout_seconds: int,
        **kwargs: Any,
    ) -> Dict[str, Any]:
        """Execute a chat completion and return a normalized response dictionary."""
        response: ChatCompletion = await self._client.chat.completions.create(  # type: ignore[call-arg]
            model=model,
            messages=messages,
            timeout=timeout_seconds,
            **kwargs,
        )

        choice = response.choices[0]
        message = choice.message
        normalized_message: Dict[str, Any] = {
            "role": message.role,
            "content": message.content or "",
        }

        tool_calls = getattr(message, "tool_calls", None)
        if tool_calls:
            normalized_message["tool_calls"] = [call.model_dump() for call in tool_calls]

        usage = response.usage.model_dump() if response.usage else None

        return {
            "message": normalized_message,
            "usage": usage,
            "raw": response.model_dump(exclude_none=True),
        }
