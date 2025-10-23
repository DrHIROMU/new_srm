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

export type ChatStreamEvent =
  | {
      type: 'content.delta'
      delta: string
    }
  | {
      type: 'content.done'
      content: string
    }
  | {
      type: 'final'
      conversation_id?: string | null
      reply: ChatMessagePayload
      context: ContextResultPayload[]
      usage?: Record<string, unknown> | null
      raw_response?: Record<string, unknown> | null
      finish_reason?: string | null
    }
  | {
      type: 'tool_calls.function.arguments.delta'
      name: string
      index: number
      arguments: string
      arguments_delta: string
    }
  | {
      type: 'tool_calls.function.arguments.done'
      name: string
      index: number
      arguments: string
      parsed_arguments: unknown
    }
  | {
      type: 'error'
      status?: number
      detail: string
    }
