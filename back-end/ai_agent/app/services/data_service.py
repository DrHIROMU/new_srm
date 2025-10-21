"""Service responsible for communicating with enterprise APIs."""

from __future__ import annotations

from typing import Any, Dict, List, Optional

import httpx

from ..models.chat import ContextRequest, ContextResult


class DataService:
    """Fetches supplemental data from enterprise APIs to enrich LLM prompts."""

    def __init__(self, base_url: Optional[str], api_key: Optional[str], timeout_seconds: int) -> None:
        self._base_url = base_url
        self._api_key = api_key
        self._timeout_seconds = timeout_seconds

    async def fetch_context(self, context_requests: List[ContextRequest]) -> List[ContextResult]:
        """Retrieve data for each requested context item."""
        results: List[ContextResult] = []
        for request in context_requests:
            data = await self._fetch_single(request)
            results.append(ContextResult(name=request.name, data=data))
        return results

    async def _fetch_single(self, request: ContextRequest) -> Any:
        """Call the configured enterprise API for a single request definition."""
        if not self._base_url:
            raise RuntimeError("EXTERNAL_API_BASE_URL is not configured.")

        headers = self._build_headers(request.headers)

        async with httpx.AsyncClient(
            base_url=self._base_url,
            timeout=self._timeout_seconds,
            headers=headers,
        ) as client:
            if request.method == "POST":
                response = await client.post(request.endpoint, json=request.payload, params=request.params)
            else:
                response = await client.get(request.endpoint, params=request.params)

        response.raise_for_status()
        return response.json()

    def _build_headers(self, override_headers: Optional[Dict[str, str]]) -> Dict[str, str]:
        """Merge default authentication headers with request-specific overrides."""
        headers: Dict[str, str] = {}
        if self._api_key:
            headers["Authorization"] = f"Bearer {self._api_key}"
        if override_headers:
            headers.update(override_headers)
        return headers
