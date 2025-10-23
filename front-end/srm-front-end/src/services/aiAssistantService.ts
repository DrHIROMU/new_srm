import type { ChatRequestPayload, ChatStreamEvent } from '@/types/aiAssistant'

const AI_AGENT_API_BASE_URL =
  import.meta.env.VITE_AI_AGENT_API_BASE_URL ?? 'http://localhost:8081/api'

const SANITIZED_BASE_URL = AI_AGENT_API_BASE_URL.replace(/\/$/, '')
const CHAT_STREAM_ENDPOINT = `${SANITIZED_BASE_URL}/chat/stream`

type StreamChatOptions = {
  signal?: AbortSignal
}

export async function* streamChatWithAssistant(
  payload: ChatRequestPayload,
  options: StreamChatOptions = {},
): AsyncGenerator<ChatStreamEvent> {
  const response = await fetch(CHAT_STREAM_ENDPOINT, {
    method: 'POST',
    headers: {
      Accept: 'text/event-stream',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(payload),
    signal: options.signal,
  })

  if (!response.ok) {
    const errorText = await response.text()
    throw new Error(errorText || `AI agent API error (${response.status})`)
  }

  if (!response.body) {
    throw new Error('Browser does not support streaming responses.')
  }

  const textReader =
    typeof TextDecoderStream !== 'undefined' && typeof response.body.pipeThrough === 'function'
      ? response.body.pipeThrough(new TextDecoderStream()).getReader()
      : null
  const reader = (textReader ?? response.body.getReader()) as ReadableStreamDefaultReader<
    string | Uint8Array
  >
  const decoder = textReader ? null : new TextDecoder()
  let buffer = ''

  const extractEvents = (input: string) => {
    const events: ChatStreamEvent[] = []
    let remaining = input
    let isDone = false

    let boundary = remaining.indexOf('\n\n')
    while (boundary !== -1) {
      const rawEvent = remaining.slice(0, boundary)
      remaining = remaining.slice(boundary + 2)

      const dataLines = rawEvent
        .split(/\r?\n/)
        .filter((line) => line.startsWith('data:'))
        .map((line) => line.slice(5).trimStart())

      if (dataLines.length > 0) {
        const payloadText = dataLines.join('\n').trim()
        if (payloadText === '[DONE]') {
          isDone = true
          break
        }
        if (payloadText) {
          try {
            events.push(JSON.parse(payloadText) as ChatStreamEvent)
          } catch {
            // Ignore malformed chunks and continue streaming.
          }
        }
      }

      boundary = remaining.indexOf('\n\n')
    }

    return { events, remaining, isDone }
  }

  try {
    while (true) {
      const { value, done } = await reader.read()
      const chunk =
        typeof value === 'string'
          ? value
          : decoder
              ? decoder.decode(value ?? new Uint8Array(), { stream: !done })
              : ''

      if (chunk) {
        const parsed = extractEvents((buffer += chunk))
        buffer = parsed.remaining

        for (const event of parsed.events) {
          yield event
        }

        if (parsed.isDone) {
          break
        }
      }

      if (done) {
        const trimmed = buffer.trim()
        if (trimmed) {
          const parsed = extractEvents(`${trimmed}\n\n`)
          for (const event of parsed.events) {
            yield event
          }
        }
        break
      }
    }
  } finally {
    if (typeof reader.releaseLock === 'function') {
      reader.releaseLock()
    }
  }
}
