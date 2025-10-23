"""Wrapper around the OpenAI SDK to simplify interactions."""

from __future__ import annotations

from typing import Any, AsyncIterator, Dict, List, Optional, TYPE_CHECKING

try:
    from openai import AsyncOpenAI, OpenAIError
except ImportError as exc:  # pragma: no cover - handled at runtime
    AsyncOpenAI = None  # type: ignore[assignment]
    OpenAIError = Exception  # type: ignore[assignment]
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

    async def stream_chat_completion(
        self,
        *,
        messages: List[Dict[str, Any]],
        model: str,
        timeout_seconds: int,
        **kwargs: Any,
    ) -> AsyncIterator[Dict[str, Any]]:
        """Stream a chat completion, yielding delta events followed by the final payload."""
        stream_options = kwargs.pop("stream_options", None) or {"include_usage": True}

        async with self._client.chat.completions.stream(  # type: ignore[call-arg]
            model=model,
            messages=messages,
            timeout=timeout_seconds,
            stream_options=stream_options,
            **kwargs,
        ) as stream:
            async for event in stream:
                if event.type == "content.delta":
                    yield {"type": "content.delta", "delta": event.delta}
                elif event.type == "content.done":
                    yield {"type": "content.done", "content": event.content}
                elif event.type == "tool_calls.function.arguments.delta":
                    yield {
                        "type": event.type,
                        "name": event.name,
                        "index": event.index,
                        "arguments": event.arguments,
                        "arguments_delta": event.arguments_delta,
                    }
                elif event.type == "tool_calls.function.arguments.done":
                    yield {
                        "type": event.type,
                        "name": event.name,
                        "index": event.index,
                        "arguments": event.arguments,
                        "parsed_arguments": event.parsed_arguments,
                    }

            final_completion = await stream.get_final_completion()
            if not final_completion.choices:
                raise OpenAIError("OpenAI returned no choices in the streamed response.")

            choice = final_completion.choices[0]
            message = choice.message
            content = message.content or ""
            if isinstance(content, list):
                content = " ".join(
                    part.get("text", "")
                    for part in content
                    if isinstance(part, dict) and part.get("type") == "text"
                )

            normalized_message: Dict[str, Any] = {
                "role": message.role,
                "content": content,
                "name": getattr(message, "name", None),
            }

            tool_calls = getattr(message, "tool_calls", None)
            if tool_calls:
                normalized_message["tool_calls"] = [call.model_dump() for call in tool_calls]

            usage = final_completion.usage.model_dump() if final_completion.usage else None

            yield {
                "type": "final",
                "message": normalized_message,
                "usage": usage,
                "raw": final_completion.model_dump(exclude_none=True),
                "finish_reason": choice.finish_reason,
            }
