import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import type { TokenResponse, User } from '@/types/auth'
import {
  exchangeCodeForToken as exchangeCodeApi,
  fetchUserInfo,
  logout as logoutApi,
  revokeToken,
} from '@/services/authService'

interface PersistedSession {
  accessToken: string | null
  refreshToken: string | null
  expiresAt: number | null
  user: User | null
}

const SESSION_STORAGE_KEY = 'srm.auth.session'

const readPersistedSession = (): PersistedSession | null => {
  const raw = localStorage.getItem(SESSION_STORAGE_KEY)
  if (!raw) {
    return null
  }
  try {
    return JSON.parse(raw) as PersistedSession
  } catch (error) {
    console.warn('Failed to parse persisted auth session', error)
    localStorage.removeItem(SESSION_STORAGE_KEY)
    return null
  }
}

export const useAuthStore = defineStore('auth', () => {
  const accessToken = ref<string | null>(null)
  const refreshToken = ref<string | null>(null)
  const tokenExpiresAt = ref<number | null>(null)
  const user = ref<User | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!accessToken.value)

  const persistSession = () => {
    const payload: PersistedSession = {
      accessToken: accessToken.value,
      refreshToken: refreshToken.value,
      expiresAt: tokenExpiresAt.value,
      user: user.value,
    }
    localStorage.setItem(SESSION_STORAGE_KEY, JSON.stringify(payload))
  }

  const clearSession = () => {
    accessToken.value = null
    refreshToken.value = null
    tokenExpiresAt.value = null
    user.value = null
    localStorage.removeItem(SESSION_STORAGE_KEY)
  }

  const applyTokenResponse = (tokenResponse: TokenResponse) => {
    accessToken.value = tokenResponse.accessToken
    refreshToken.value = tokenResponse.refreshToken
    tokenExpiresAt.value = tokenResponse.expiresIn ? Date.now() + tokenResponse.expiresIn * 1000 : null
    persistSession()
  }

  const restoreSessionFromStorage = () => {
    const persisted = readPersistedSession()
    if (!persisted) {
      return
    }
    accessToken.value = persisted.accessToken
    refreshToken.value = persisted.refreshToken
    tokenExpiresAt.value = persisted.expiresAt
    user.value = persisted.user
  }

  const determineErrorMessage = (cause: unknown): string => {
    if (cause && typeof cause === 'object' && 'response' in cause) {
      const response = (cause as any).response
      const detail = response?.data?.error_description || response?.data?.message
      if (typeof detail === 'string') {
        return detail
      }
    }
    return '驗證流程發生錯誤，請稍後再試'
  }

  const exchangeCodeForToken = async (code: string, codeVerifier: string): Promise<boolean> => {
    isLoading.value = true
    error.value = null
    try {
      const tokenResponse = await exchangeCodeApi({ code, codeVerifier })
      applyTokenResponse(tokenResponse)
      return true
    } catch (err) {
      console.error('Token exchange failed', err)
      clearSession()
      error.value = determineErrorMessage(err)
      return false
    } finally {
      isLoading.value = false
    }
  }

  const fetchProfile = async (): Promise<User | null> => {
    try {
      const profile = await fetchUserInfo()
      user.value = profile
      persistSession()
      return profile
    } catch (err) {
      console.error('Failed to load user profile', err)
      error.value = determineErrorMessage(err)
      return null
    }
  }

  const logout = async (): Promise<void> => {
    isLoading.value = true
    try {
      if (accessToken.value) {
        await revokeToken(accessToken.value)
      }
    } catch (err) {
      console.warn('Token revocation failed', err)
    } finally {
      try {
        await logoutApi()
      } catch (err) {
        console.warn('Logout endpoint failed', err)
      } finally {
        clearSession()
        isLoading.value = false
      }
    }
  }

  return {
    accessToken,
    refreshToken,
    tokenExpiresAt,
    user,
    isLoading,
    error,
    isAuthenticated,
    restoreSessionFromStorage,
    exchangeCodeForToken,
    fetchProfile,
    logout,
    clearSession,
  }
})
