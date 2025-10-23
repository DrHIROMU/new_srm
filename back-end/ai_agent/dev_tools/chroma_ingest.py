"""CLI utility to ingest local documents into a ChromaDB collection."""

from __future__ import annotations

import argparse
import logging
from pathlib import Path
from typing import Iterable, List
from uuid import uuid4

import chromadb
from chromadb.utils import embedding_functions

DEFAULT_EXTENSIONS = [".txt", ".md"]

logger = logging.getLogger(__name__)


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Ingest text documents into ChromaDB.")
    parser.add_argument("source", type=Path, help="Directory containing text files to ingest.")
    parser.add_argument("--chroma-host", default="127.0.0.1", help="ChromaDB host.")
    parser.add_argument("--chroma-port", type=int, default=8000, help="ChromaDB port.")
    parser.add_argument(
        "--collection",
        default="srm-knowledge-base",
        help="Target ChromaDB collection name.",
    )
    parser.add_argument(
        "--embedding-model",
        default="sentence-transformers/all-MiniLM-L6-v2",
        help="SentenceTransformer model used for embeddings.",
    )
    parser.add_argument(
        "--extensions",
        nargs="*",
        default=DEFAULT_EXTENSIONS,
        help="List of file extensions to include (default: .txt .md).",
    )
    parser.add_argument(
        "--chunk-size",
        type=int,
        default=500,
        help="Approximate number of words per chunk.",
    )
    parser.add_argument(
        "--chunk-overlap",
        type=int,
        default=80,
        help="Word overlap between consecutive chunks.",
    )
    parser.add_argument(
        "--reset",
        action="store_true",
        help="If set, clears the entire collection before ingesting.",
    )
    return parser.parse_args()


def iter_files(directory: Path, extensions: Iterable[str]) -> Iterable[Path]:
    normalized_exts = {ext.lower() if ext.startswith(".") else f".{ext.lower()}" for ext in extensions}
    for path in directory.rglob("*"):
        if path.is_file() and path.suffix.lower() in normalized_exts:
            yield path


def chunk_text(text: str, chunk_size: int, chunk_overlap: int) -> List[str]:
    words = text.split()
    if not words:
        return []

    chunk_size = max(1, chunk_size)
    chunk_overlap = max(0, min(chunk_overlap, chunk_size - 1))

    chunks: List[str] = []
    start = 0
    step = chunk_size - chunk_overlap
    while start < len(words):
        end = start + chunk_size
        chunk = " ".join(words[start:end]).strip()
        if chunk:
            chunks.append(chunk)
        start += step
    return chunks


def main() -> None:
    logging.basicConfig(level=logging.INFO, format="%(levelname)s %(message)s")
    args = parse_args()

    if not args.source.exists():
        raise SystemExit(f"Source directory '{args.source}' does not exist.")

    client = chromadb.HttpClient(host=args.chroma_host, port=args.chroma_port)
    embedding_fn = embedding_functions.SentenceTransformerEmbeddingFunction(model_name=args.embedding_model)
    collection = client.get_or_create_collection(name=args.collection, embedding_function=embedding_fn)

    if args.reset:
        logger.info("Clearing collection '%s' before ingesting.", args.collection)
        collection.delete()

    files = list(iter_files(args.source, args.extensions))
    if not files:
        logger.warning("No documents found in %s with extensions %s.", args.source, args.extensions)
        return

    for file_path in files:
        try:
            text = file_path.read_text(encoding="utf-8")
        except UnicodeDecodeError:
            text = file_path.read_text(encoding="utf-8", errors="ignore")

        chunks = chunk_text(text, args.chunk_size, args.chunk_overlap)
        if not chunks:
            logger.info("Skipping empty document: %s", file_path)
            continue

        logger.info("Ingesting %s with %d chunks.", file_path, len(chunks))
        collection.delete(where={"source_path": str(file_path)})

        ids = [f"{file_path.name}-{idx}-{uuid4().hex}" for idx in range(len(chunks))]
        metadatas = [
            {
                "source_path": str(file_path),
                "chunk_index": idx,
                "total_chunks": len(chunks),
            }
            for idx in range(len(chunks))
        ]

        collection.add(ids=ids, documents=chunks, metadatas=metadatas)

    logger.info("Ingestion complete. Collection '%s' now contains %d items.", args.collection, collection.count())


if __name__ == "__main__":
    main()

