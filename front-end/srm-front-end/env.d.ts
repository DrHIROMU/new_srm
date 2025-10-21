/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_AI_AGENT_API_BASE_URL?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
