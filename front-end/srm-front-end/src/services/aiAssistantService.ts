import axios from 'axios'
import type { ChatRequestPayload, ChatResponsePayload } from '@/types/aiAssistant'

const AI_AGENT_API_BASE_URL =
  import.meta.env.VITE_AI_AGENT_API_BASE_URL ?? 'http://localhost:8081/api'

const aiAgentClient = axios.create({
  baseURL: AI_AGENT_API_BASE_URL,
  timeout: 20000,
})

export const chatWithAssistant = async (
  payload: ChatRequestPayload,
): Promise<ChatResponsePayload> => {
  const { data } = await aiAgentClient.post<ChatResponsePayload>('/chat', payload)
  return data
}
