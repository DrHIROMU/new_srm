"""Dependency factory functions for FastAPI."""

from functools import lru_cache

from .clients.openai_client import OpenAIChatClient
from .core.config import settings
from .services.data_service import DataService
from .services.knowledge_service import KnowledgeService
from .services.llm_service import LLMService


@lru_cache
def _get_openai_client() -> OpenAIChatClient:
    """Return a cached OpenAI client instance."""
    return OpenAIChatClient(api_key=settings.openai_api_key)


@lru_cache
def get_data_service() -> DataService:
    """Return a cached DataService instance."""
    return DataService(
        base_url=settings.external_api_base_url,
        api_key=settings.external_api_key,
        timeout_seconds=settings.external_api_timeout_seconds,
    )


@lru_cache
def get_knowledge_service() -> KnowledgeService:
    """Return a cached KnowledgeService instance."""
    return KnowledgeService(
        host=settings.chroma_host,
        port=settings.chroma_port,
        collection=settings.chroma_collection,
        embedding_model=settings.chroma_embedding_model,
        top_k=settings.chroma_top_k,
        enabled=settings.knowledge_search_enabled,
    )


def get_llm_service() -> LLMService:
    """Construct an LLM service with dependencies injected."""
    return LLMService(
        client=_get_openai_client(),
        default_model=settings.openai_model,
        timeout_seconds=settings.openai_timeout_seconds,
        knowledge_service=get_knowledge_service(),
    )
