# AI Agent Backend

Python FastAPI service that brokers conversations between the SRM front-end, OpenAI, and internal APIs. It exposes a lightweight REST surface that the UI can call to initiate LLM chats while requesting supplemental data from enterprise systems.

## Prerequisites

- Python 3.11 or later
- OpenAI API key with access to the desired chat models
- Access credentials for the enterprise APIs the agent will query

## Project Structure

- `app/main.py` - FastAPI application factory
- `app/api/routes.py` - HTTP endpoints (`/api/chat`, `/api/health`)
- `app/models/` - Pydantic models shared by the application
- `app/services/` - Domain services for OpenAI and enterprise APIs
- `app/clients/openai_client.py` - Thin wrapper over the OpenAI SDK
- `tests/` - Pytest + FastAPI TestClient smoke coverage
- `.env.example` - Template for required environment variables
- `pyproject.toml` - Project metadata and dependencies

## Getting Started

1. Create a virtual environment and activate it.
2. Install dependencies with `pip install -e .[dev]` from this directory.
3. Copy `.env.example` to `.env` and populate the secrets:
   - `OPENAI_API_KEY`, `OPENAI_MODEL`, `OPENAI_TIMEOUT_SECONDS`
   - `EXTERNAL_API_BASE_URL`, `EXTERNAL_API_KEY`, `EXTERNAL_API_TIMEOUT_SECONDS`
   - `CORS_ALLOW_ORIGINS` (comma-separated list of allowed front-end origins, e.g. `http://localhost:4200`)
4. Launch the API using `uvicorn app.main:app --reload --host 0.0.0.0 --port 8081`.

The service will expose OpenAPI docs at `http://localhost:8081/api/docs`.

## Testing

```
pytest
```

Tests use dependency overrides to avoid real OpenAI or enterprise API calls.

## Next Steps

- Wire `DataService` (`app/services/data_service.py`) to the concrete endpoints that will be provided.
- Decide on authentication strategy for the front-end (`Authorization` headers, session tokens, etc.).
- Extend `LLMService` if tool-calling or function-calling workflows are needed.
- Add deployment automation once the integration paths are stable (Dockerfile, CI stages, etc.).
