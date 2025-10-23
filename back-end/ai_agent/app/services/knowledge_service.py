"""Service that connects to ChromaDB to provide retrieval-augmented context."""

from __future__ import annotations

import logging
from typing import List, Optional

import chromadb
from chromadb.errors import ChromaError
from chromadb.utils import embedding_functions

from ..models.chat import ContextResult

logger = logging.getLogger(__name__)


class KnowledgeService:
    """Small wrapper around ChromaDB for semantic search over curated documents."""

    def __init__(
        self,
        *,
        host: Optional[str],
        port: int,
        collection: str,
        embedding_model: str,
        top_k: int,
        enabled: bool,
    ) -> None:
        self._enabled = enabled and bool(host)
        self._top_k = max(1, top_k)
        self._collection = None

        if not self._enabled:
            logger.info("Knowledge search disabled; skipping ChromaDB initialization.")
            return

        try:
            client = chromadb.HttpClient(host=str(host), port=port)
            embedding_fn = embedding_functions.SentenceTransformerEmbeddingFunction(
                model_name=embedding_model
            )
            self._collection = client.get_or_create_collection(
                name=collection,
                embedding_function=embedding_fn,
            )
            logger.info(
                "Connected to ChromaDB collection '%s' on %s:%s", collection, host, port
            )
        except Exception as exc:  # pragma: no cover - defensive guard
            logger.exception("Unable to initialise ChromaDB client: %s", exc)
            self._enabled = False

    @property
    def enabled(self) -> bool:
        """Return whether knowledge search is available."""
        return self._enabled and self._collection is not None

    def search(self, query: str) -> List[ContextResult]:
        """Search the knowledge base for relevant passages."""
        if not self.enabled:
            return []
        normalized_query = (query or "").strip()
        if not normalized_query:
            return []

        try:
            results = self._collection.query(
                query_texts=[normalized_query],
                n_results=self._top_k,
                include=["documents", "metadatas", "distances"],
            )
        except ChromaError as exc:
            logger.error("ChromaDB query failed: %s", exc)
            return []
        except Exception as exc:  # pragma: no cover - defensive guard
            logger.exception("Unexpected error querying ChromaDB", exc_info=exc)
            return []

        documents = results.get("documents") or [[]]
        metadatas = results.get("metadatas") or [[]]
        distances = results.get("distances") or [[]]

        matches = []
        for idx, content in enumerate(documents[0]):
            if not content:
                continue
            metadata = metadatas[0][idx] if idx < len(metadatas[0]) else {}
            distance = distances[0][idx] if idx < len(distances[0]) else None
            score = None
            if isinstance(distance, (int, float)):
                score = 1 / (1 + distance)

            matches.append(
                {
                    "content": content,
                    "metadata": metadata or {},
                    "distance": distance,
                    "similarity_score": score,
                }
            )

        if not matches:
            return []

        return [ContextResult(name="knowledge_base", data={"matches": matches})]

