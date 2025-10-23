<template>
  <Teleport to="body">
    <div class="ai-assistant-root">
      <Transition name="ai-slide-up">
        <section
          v-if="isOpen"
          class="ai-assistant-panel"
          role="dialog"
          aria-modal="true"
          aria-label="AI 助手對話框"
        >
          <header class="ai-assistant-header">
            <div>
              <p class="ai-assistant-title">AI 助手</p>
              <p class="ai-assistant-subtitle">有任何採購問題，都可以問我</p>
            </div>
            <button
              type="button"
              class="ai-assistant-close"
              @click="closePanel"
              aria-label="關閉對話框"
            >
              ✕
            </button>
          </header>

          <div class="ai-assistant-body">
            <div class="ai-assistant-messages" ref="messagesContainer" tabindex="-1">
              <div
                v-for="message in messages"
                :key="message.id"
                class="ai-assistant-message"
                :class="`ai-assistant-message--${message.role}`"
              >
                <div class="ai-assistant-bubble">
                  <p>{{ displayedContent[message.id] ?? message.content }}</p>
                </div>
              </div>
              <p v-if="showTypingIndicator" class="ai-assistant-typing">AI 助手正在思考...</p>
            </div>

            <p v-if="errorMessage" class="ai-assistant-error" role="alert">
              {{ errorMessage }}
            </p>
          </div>

          <form class="ai-assistant-composer" @submit.prevent="handleSubmit">
            <textarea
              ref="inputRef"
              v-model="userInput"
              class="ai-assistant-input"
              placeholder="輸入訊息與 AI 助手對話..."
              rows="3"
              @keydown.enter.exact.prevent="handleEnterKey"
            />
            <div class="ai-assistant-actions">
              <button type="submit" class="ai-assistant-send" :disabled="!canSend">
                發送
              </button>
            </div>
          </form>
        </section>
      </Transition>

      <button
        type="button"
        class="ai-assistant-toggle"
        @click="togglePanel"
        aria-label="開啟 AI 助手"
      >
        <span class="ai-assistant-toggle__icon">🤖</span>
      </button>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, reactive, ref, watch } from 'vue'
import axios from 'axios'
import { streamChatWithAssistant } from '@/services/aiAssistantService'
import type { ChatMessage, ChatStreamEvent } from '@/types/aiAssistant'

const isOpen = ref(false)
const isSending = ref(false)
const userInput = ref('')
const errorMessage = ref<string | null>(null)
const conversationId = ref<string | null>(null)
const messages = ref<ChatMessage[]>([
  {
    id: generateId('assistant'),
    role: 'assistant',
    content: '您好，我是 AI 採購助手，需要協助什麼嗎？',
  },
])
const messagesContainer = ref<HTMLElement | null>(null)
const inputRef = ref<HTMLTextAreaElement | null>(null)
const streamAbortController = ref<AbortController | null>(null)
const displayedContent = reactive<Record<string, string>>({})
const TYPEWRITER_MIN_INTERVAL_MS = 20

type TypewriterState = {
  queue: string[]
  frameId: number | null
  lastRenderedAt?: number
}

const typewriterStates = new Map<string, TypewriterState>()
const streamingMessageId = ref<string | null>(null)

messages.value.forEach((message) => {
  displayedContent[message.id] = message.content
})

const setDisplayedContent = (id: string, content: string) => {
  displayedContent[id] = content
}

const stopTypewriter = (id: string) => {
  const state = typewriterStates.get(id)
  if (!state) {
    return
  }
  if (typeof window !== 'undefined' && state.frameId !== null) {
    window.cancelAnimationFrame(state.frameId)
  }
  state.queue.length = 0
  typewriterStates.delete(id)
}

const getBufferedContent = (id: string) => {
  const pending = typewriterStates.get(id)
  const pendingText = pending ? pending.queue.join('') : ''
  return (displayedContent[id] ?? '') + pendingText
}

const scheduleTypewriter = (id: string) => {
  const state = typewriterStates.get(id)
  if (!state) {
    return
  }

  if (typeof window === 'undefined') {
    if (state.queue.length > 0) {
      setDisplayedContent(id, (displayedContent[id] ?? '') + state.queue.join(''))
      state.queue.length = 0
    }
    typewriterStates.delete(id)
    return
  }

  if (state.frameId !== null) {
    return
  }

  const step = (timestamp: number) => {
    const currentState = typewriterStates.get(id)
    if (!currentState) {
      return
    }

    if (
      currentState.lastRenderedAt !== undefined &&
      timestamp - currentState.lastRenderedAt < TYPEWRITER_MIN_INTERVAL_MS
    ) {
      currentState.frameId = window.requestAnimationFrame(step)
      return
    }

    currentState.lastRenderedAt = timestamp

    const nextCharacter = currentState.queue.shift() ?? ''
    if (nextCharacter) {
      setDisplayedContent(id, (displayedContent[id] ?? '') + nextCharacter)
      nextTick(scrollMessagesToBottom)
    }

    if (currentState.queue.length === 0) {
      currentState.frameId = null
      currentState.lastRenderedAt = undefined
      typewriterStates.delete(id)
      if (streamingMessageId.value === id) {
        streamingMessageId.value = null
      }
      return
    }

    currentState.frameId = window.requestAnimationFrame(step)
  }

  state.frameId = window.requestAnimationFrame(step)
}

const enqueueTypewriter = (id: string, text: string) => {
  if (!text) {
    return
  }

  const characters = Array.from(text)
  if (characters.length === 0) {
    return
  }

  const existingState = typewriterStates.get(id)
  const state: TypewriterState =
    existingState ?? { queue: [], frameId: null, lastRenderedAt: undefined }
  state.queue.push(...characters)
  typewriterStates.set(id, state)

  scheduleTypewriter(id)
}

const finalizeTypewriter = (id: string, content: string) => {
  stopTypewriter(id)
  setDisplayedContent(id, content)
}

const removeDisplayedContent = (id: string) => {
  stopTypewriter(id)
  if (Object.prototype.hasOwnProperty.call(displayedContent, id)) {
    delete displayedContent[id]
  }
}

const queueContentDiff = (id: string, targetContent: string) => {
  if (!targetContent) {
    finalizeTypewriter(id, '')
    return
  }

  const bufferedContent = getBufferedContent(id)
  if (bufferedContent === targetContent) {
    return
  }

  if (targetContent.startsWith(bufferedContent)) {
    const remainder = targetContent.slice(bufferedContent.length)
    if (remainder) {
      enqueueTypewriter(id, remainder)
    }
    return
  }

  finalizeTypewriter(id, targetContent)
}

const canSend = computed(() => userInput.value.trim().length > 0 && !isSending.value)
const showTypingIndicator = computed(() => {
  const lastMessage = messages.value[messages.value.length - 1]
  if (!lastMessage || lastMessage.role !== 'assistant') {
    return isSending.value
  }
  const renderedContent = displayedContent[lastMessage.id] ?? lastMessage.content
  if (renderedContent !== lastMessage.content) {
    return true
  }
  return isSending.value && !lastMessage.content
})

const scrollMessagesToBottom = () => {
  const container = messagesContainer.value
  if (container) {
    container.scrollTop = container.scrollHeight
  }
}

const abortActiveStream = () => {
  if (streamAbortController.value) {
    streamAbortController.value.abort()
    streamAbortController.value = null
  }
  if (streamingMessageId.value) {
    stopTypewriter(streamingMessageId.value)
    streamingMessageId.value = null
  }
}

watch(
  () => messages.value.length,
  async () => {
    await nextTick()
    scrollMessagesToBottom()
  },
)

watch(isOpen, async (open) => {
  if (open) {
    await nextTick()
    inputRef.value?.focus()
  }
})

const togglePanel = () => {
  if (isOpen.value) {
    abortActiveStream()
  }
  isOpen.value = !isOpen.value
}

const closePanel = () => {
  abortActiveStream()
  isOpen.value = false
}

const handleEnterKey = () => {
  if (canSend.value) {
    handleSubmit()
  }
}

const applyStreamEvent = async (
  event: ChatStreamEvent,
  assistantMessage: ChatMessage,
): Promise<boolean> => {
  switch (event.type) {
    case 'content.delta': {
      if (event.delta) {
        assistantMessage.content += event.delta
        enqueueTypewriter(assistantMessage.id, event.delta)
      }
      break
    }
    case 'content.done': {
      const nextContent = event.content ?? assistantMessage.content
      assistantMessage.content = nextContent
      queueContentDiff(assistantMessage.id, nextContent)
      break
    }
    case 'final': {
      conversationId.value = event.conversation_id ?? conversationId.value
      assistantMessage.role = event.reply.role ?? 'assistant'
      const targetContent =
        event.reply.content?.trim() ||
        assistantMessage.content ||
        '我已收到您的訊息。'
      assistantMessage.content = targetContent
      queueContentDiff(assistantMessage.id, targetContent)
      if (!typewriterStates.has(assistantMessage.id)) {
        streamingMessageId.value = null
      }
      return true
    }
    case 'error': {
      const detail = event.detail || 'AI �U��ȮɵL�k�^���A�еy��A�աC'
      errorMessage.value = detail
      assistantMessage.content = detail
      finalizeTypewriter(assistantMessage.id, detail)
      streamingMessageId.value = null
      return true
    }
    default:
      break
  }

  await nextTick()
  scrollMessagesToBottom()
  return false
}


const handleSubmit = async () => {
  const message = userInput.value.trim()
  if (!message || isSending.value) {
    return
  }

  const userMessage: ChatMessage = {
    id: generateId('user'),
    role: 'user',
    content: message,
  }

  messages.value.push(userMessage)
  setDisplayedContent(userMessage.id, userMessage.content)
  await nextTick()
  scrollMessagesToBottom()

  userInput.value = ''
  errorMessage.value = null
  isSending.value = true

  if (!conversationId.value) {
    conversationId.value = generateId('conversation')
  }

  const requestMessages = messages.value.map(({ role, content }) => ({
    role,
    content,
  }))

  const assistantMessage: ChatMessage = {
    id: generateId('assistant'),
    role: 'assistant',
    content: '',
  }
  messages.value.push(assistantMessage)
  setDisplayedContent(assistantMessage.id, '')
  await nextTick()
  scrollMessagesToBottom()

  abortActiveStream()
  const abortController = new AbortController()
  streamAbortController.value = abortController
  streamingMessageId.value = assistantMessage.id

  try {
    for await (const event of streamChatWithAssistant(
      {
        conversation_id: conversationId.value,
        messages: requestMessages,
      },
      { signal: abortController.signal },
    )) {
      const isFinal = await applyStreamEvent(event, assistantMessage)
      if (isFinal) {
        break
      }
    }

    if (!assistantMessage.content.trim()) {
      assistantMessage.content = '我已收到您的訊息。'
      finalizeTypewriter(assistantMessage.id, assistantMessage.content)
    }
  } catch (err) {
    if (isAbortError(err)) {
      if (!assistantMessage.content.trim()) {
        removeDisplayedContent(assistantMessage.id)
        messages.value = messages.value.filter((item) => item.id !== assistantMessage.id)
      } else {
        finalizeTypewriter(assistantMessage.id, assistantMessage.content)
      }
    } else {
      errorMessage.value = resolveErrorMessage(err)
      assistantMessage.content = '目前無法連線到 AI 助手，請稍後再試一次。'
      finalizeTypewriter(assistantMessage.id, assistantMessage.content)
    }
  } finally {
    if (streamAbortController.value === abortController) {
      streamAbortController.value = null
    }
    streamingMessageId.value = null
    isSending.value = false
    await nextTick()
    scrollMessagesToBottom()
  }
}

const isAbortError = (err: unknown): err is DOMException => {
  return err instanceof DOMException && err.name === "AbortError"
}

const resolveErrorMessage = (err: unknown) => {
  if (axios.isAxiosError(err)) {
    const detail =
      (typeof err.response?.data === 'string' && err.response.data) ||
      err.response?.data?.detail ||
      err.message
    return detail || '發送失敗，請稍後再試。'
  }
  if (err instanceof Error) {
    return err.message
  }
  return '發送失敗，請稍後再試。'
}

function generateId(prefix: string) {
  const fallback = `${Date.now()}-${Math.random().toString(16).slice(2)}`
  if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
    return `${prefix}-${crypto.randomUUID()}`
  }
  return `${prefix}-${fallback}`
}

onBeforeUnmount(() => {
  abortActiveStream()
})
</script>

<style scoped>
.ai-assistant-root {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1050;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  pointer-events: none;
}

.ai-assistant-root > * {
  pointer-events: auto;
}

.ai-assistant-panel {
  width: min(360px, calc(100vw - 32px));
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 24px 48px rgba(15, 23, 42, 0.22);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-assistant-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 20px 12px;
  border-bottom: 1px solid #e2e8f0;
}

.ai-assistant-title {
  font-size: 16px;
  font-weight: 600;
  color: #0f172a;
  margin: 0;
}

.ai-assistant-subtitle {
  font-size: 12px;
  color: #64748b;
  margin: 4px 0 0;
}

.ai-assistant-close {
  border: none;
  background: transparent;
  color: #64748b;
  font-size: 16px;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: background 0.2s ease;
}

.ai-assistant-close:hover {
  background: rgba(148, 163, 184, 0.16);
}

.ai-assistant-body {
  display: flex;
  flex-direction: column;
  padding: 12px 20px 0;
  gap: 8px;
}

.ai-assistant-messages {
  max-height: 320px;
  overflow-y: auto;
  padding-right: 4px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ai-assistant-message {
  display: flex;
}

.ai-assistant-message--user {
  justify-content: flex-end;
}

.ai-assistant-message--assistant,
.ai-assistant-message--system,
.ai-assistant-message--tool {
  justify-content: flex-start;
}

.ai-assistant-bubble {
  background: #1d4ed8;
  color: #ffffff;
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.4;
  max-width: 240px;
  word-break: break-word;
}

.ai-assistant-message--assistant .ai-assistant-bubble,
.ai-assistant-message--system .ai-assistant-bubble,
.ai-assistant-message--tool .ai-assistant-bubble {
  background: #f8fafc;
  color: #0f172a;
  border: 1px solid #e2e8f0;
}

.ai-assistant-typing {
  font-size: 12px;
  color: #64748b;
  text-align: left;
}

.ai-assistant-error {
  font-size: 12px;
  color: #dc2626;
  margin: 0;
}

.ai-assistant-composer {
  display: flex;
  flex-direction: column;
  padding: 16px 20px 20px;
  gap: 8px;
  border-top: 1px solid #e2e8f0;
}

.ai-assistant-input {
  width: 100%;
  border: 1px solid #cbd5f5;
  border-radius: 12px;
  padding: 10px 12px;
  font-size: 14px;
  resize: none;
  font-family: inherit;
  line-height: 1.4;
}

.ai-assistant-input:focus {
  outline: none;
  border-color: #2563eb;
  box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.16);
}

.ai-assistant-actions {
  display: flex;
  justify-content: flex-end;
}

.ai-assistant-send {
  background: #2563eb;
  color: #ffffff;
  border: none;
  border-radius: 12px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s ease;
}

.ai-assistant-send:disabled {
  background: #94a3b8;
  cursor: not-allowed;
}

.ai-assistant-send:not(:disabled):hover {
  background: #1d4ed8;
}

.ai-assistant-toggle {
  background: linear-gradient(135deg, #2563eb, #7c3aed);
  color: #ffffff;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  cursor: pointer;
  box-shadow: 0 12px 24px rgba(79, 70, 229, 0.35);
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.ai-assistant-toggle:hover {
  transform: translateY(-2px);
  box-shadow: 0 16px 32px rgba(79, 70, 229, 0.45);
}

.ai-assistant-toggle__icon {
  display: inline-flex;
}

.ai-slide-up-enter-active,
.ai-slide-up-leave-active {
  transition: opacity 0.2s ease, transform 0.2s ease;
}

.ai-slide-up-enter-from,
.ai-slide-up-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

@media (max-width: 640px) {
  .ai-assistant-root {
    right: 16px;
    bottom: 16px;
  }

  .ai-assistant-panel {
    width: min(100vw - 24px, 320px);
  }
}
</style>
