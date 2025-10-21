export type ChatRole = 'system' | 'user' | 'assistant' | 'tool'

export interface ChatMessage {
  id: string
  role: ChatRole
  content: string
}

export interface ChatMessagePayload {
  role: ChatRole
  content: string
  name?: string | null
}

export interface ContextRequestPayload {
  name: string
  endpoint: string
  method?: 'GET' | 'POST'
  params?: Record<string, unknown> | null
  payload?: Record<string, unknown> | null
  headers?: Record<string, string> | null
}

export interface ContextResultPayload {
  name: string
  data: unknown
}

export interface ChatRequestPayload {
  conversation_id?: string | null
  messages: ChatMessagePayload[]
  context_requests?: ContextRequestPayload[] | null
  metadata?: Record<string, unknown> | null
}

export interface ChatResponsePayload {
  conversation_id?: string | null
  reply: ChatMessagePayload
  context: ContextResultPayload[]
  usage?: Record<string, unknown> | null
  raw_response?: Record<string, unknown> | null
}
