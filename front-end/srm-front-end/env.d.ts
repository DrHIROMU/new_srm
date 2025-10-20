/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_BFF_BASE_URL?: string
  readonly VITE_BFF_LOGIN_PATH?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}
