import axios from 'axios'
import type { User } from '@/types/auth'

const bffBaseUrl = import.meta.env.VITE_BFF_BASE_URL || '/bff'
const loginPath = import.meta.env.VITE_BFF_LOGIN_PATH || '/bff/auth/login'

const authClient = axios.create({
  baseURL: bffBaseUrl,
  timeout: 10000,
  withCredentials: true,
})

export const startLogin = (): void => {
  window.location.assign(loginPath)
}

export const fetchSession = async (): Promise<User> => {
  const response = await authClient.get<User>('/auth/status')
  return response.data
}

export const logout = async (): Promise<void> => {
  await authClient.post('/auth/logout')
}
