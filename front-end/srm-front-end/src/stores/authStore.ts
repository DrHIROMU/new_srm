import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { User } from '@/types/auth'
import { fetchSession, logout as logoutApi, startLogin as redirectToBffLogin } from '@/services/authService'

const isUnauthorizedError = (cause: unknown): boolean => {
  if (cause && typeof cause === 'object' && 'response' in cause) {
    const response = (cause as any).response
    return response?.status === 401
  }
  return false
}

const extractErrorMessage = (cause: unknown): string => {
  if (cause && typeof cause === 'object' && 'response' in cause) {
    const response = (cause as any).response
    const detail = response?.data?.message || response?.data?.error_description
    if (typeof detail === 'string' && detail.trim().length > 0) {
      return detail
    }
  }
  return '驗證流程發生錯誤，請稍後再試'
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)
  const sessionChecked = ref(false)

  const isAuthenticated = computed(() => user.value !== null)

  const clearSession = () => {
    user.value = null
  }

  const refreshSession = async (): Promise<boolean> => {
    try {
      const session = await fetchSession()
      user.value = session
      error.value = null
      sessionChecked.value = true
      return true
    } catch (err) {
      if (isUnauthorizedError(err)) {
        clearSession()
        sessionChecked.value = true
        error.value = null
        return false
      }
      error.value = extractErrorMessage(err)
      sessionChecked.value = true
      throw err
    }
  }

  const initializeSession = async (): Promise<void> => {
    if (sessionChecked.value) {
      return
    }
    try {
      await refreshSession()
    } catch (err) {
      console.warn('Unable to initialize auth session', err)
    }
  }

  const startLogin = () => {
    redirectToBffLogin()
  }

  const logout = async (): Promise<void> => {
    isLoading.value = true
    error.value = null
    try {
      await logoutApi()
    } catch (err) {
      console.warn('Logout request failed', err)
    } finally {
      clearSession()
      sessionChecked.value = false
      isLoading.value = false
    }
  }

  return {
    user,
    isLoading,
    error,
    sessionChecked,
    isAuthenticated,
    initializeSession,
    refreshSession,
    startLogin,
    logout,
    clearSession,
  }
})
