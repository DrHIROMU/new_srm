"""Application configuration handled via environment variables."""

from functools import lru_cache
from typing import List, Optional

from pydantic import Field
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Configuration values loaded from environment variables or defaults."""

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    project_name: str = "SRM AI Agent"
    project_version: str = "0.1.0"

    openai_api_key: Optional[str] = Field(default=None, alias="OPENAI_API_KEY")
    openai_model: str = Field(default="gpt-4o-mini", alias="OPENAI_MODEL")
    openai_timeout_seconds: int = Field(default=30, alias="OPENAI_TIMEOUT_SECONDS")

    external_api_base_url: Optional[str] = Field(default=None, alias="EXTERNAL_API_BASE_URL")
    external_api_key: Optional[str] = Field(default=None, alias="EXTERNAL_API_KEY")
    external_api_timeout_seconds: int = Field(default=15, alias="EXTERNAL_API_TIMEOUT_SECONDS")

    cors_allow_origins: Optional[str] = Field(default=None, alias="CORS_ALLOW_ORIGINS")

    knowledge_search_enabled: bool = Field(default=False, alias="KNOWLEDGE_SEARCH_ENABLED")
    chroma_host: Optional[str] = Field(default=None, alias="CHROMA_HOST")
    chroma_port: int = Field(default=8000, alias="CHROMA_PORT")
    chroma_collection: str = Field(default="srm-knowledge-base", alias="CHROMA_COLLECTION")
    chroma_embedding_model: str = Field(
        default="sentence-transformers/all-MiniLM-L6-v2",
        alias="CHROMA_EMBEDDING_MODEL",
    )
    chroma_top_k: int = Field(default=4, alias="CHROMA_TOP_K")

    @property
    def cors_allow_origin_list(self) -> List[str]:
        """Return parsed CORS allow origins list with sensible defaults."""
        if not self.cors_allow_origins:
            return ["http://localhost:4200"]

        parsed = [origin.strip() for origin in self.cors_allow_origins.split(",") if origin.strip()]
        return parsed or ["http://localhost:4200"]

@lru_cache
def get_settings() -> Settings:
    """Return a cached Settings instance."""
    return Settings()


settings = get_settings()
