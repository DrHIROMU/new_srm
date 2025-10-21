"""Chat-related request and response models."""

from typing import Any, Dict, List, Literal, Optional

from pydantic import BaseModel, Field


class Message(BaseModel):
    """Represents a single chat message exchanged with the LLM."""

    role: Literal["system", "user", "assistant", "tool"]
    content: str
    name: Optional[str] = None


class ContextRequest(BaseModel):
    """Instruction describing which enterprise data should be retrieved."""

    name: str
    endpoint: str
    method: Literal["GET", "POST"] = "GET"
    params: Optional[Dict[str, Any]] = None
    payload: Optional[Dict[str, Any]] = None
    headers: Optional[Dict[str, str]] = None


class ContextResult(BaseModel):
    """Normalized payload returned from enterprise data lookups."""

    name: str
    data: Any


class ChatRequest(BaseModel):
    """Payload received from the front-end to drive an LLM interaction."""

    messages: List[Message] = Field(default_factory=list)
    conversation_id: Optional[str] = None
    context_requests: Optional[List[ContextRequest]] = None
    metadata: Optional[Dict[str, Any]] = None


class ChatResponse(BaseModel):
    """Response returned to the front-end after generating an LLM reply."""

    conversation_id: Optional[str] = None
    reply: Message
    context: List[ContextResult] = Field(default_factory=list)
    usage: Optional[Dict[str, Any]] = None
    raw_response: Optional[Dict[str, Any]] = None

