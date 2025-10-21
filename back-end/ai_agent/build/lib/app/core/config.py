"""Application configuration handled via environment variables."""

from functools import lru_cache
from typing import Optional

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


@lru_cache
def get_settings() -> Settings:
    """Return a cached Settings instance."""
    return Settings()


settings = get_settings()

