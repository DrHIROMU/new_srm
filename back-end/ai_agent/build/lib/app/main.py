"""Application entry point."""

from fastapi import FastAPI

from .api.routes import router as api_router
from .core.config import settings


def create_application() -> FastAPI:
    """Configure and return the FastAPI application."""
    application = FastAPI(
        title=settings.project_name,
        version=settings.project_version,
        docs_url="/api/docs",
        redoc_url="/api/redoc",
        openapi_url="/api/openapi.json",
    )

    application.include_router(api_router, prefix="/api")
    return application


app = create_application()

