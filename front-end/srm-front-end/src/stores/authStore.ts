import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin } from '@/services/authService'
import type { User, LoginCredentials } from '@/types/auth'


export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const user = ref<User | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  async function login(credentials: LoginCredentials) {
    isLoading.value = true
    error.value = null
    try {
      const response = await apiLogin(credentials)
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('authToken', token.value)
      return true
    } catch (e: any) {
      error.value = e.response?.data?.message || '登入失敗，請稍後再試'
      return false
    } finally {
      isLoading.value = false
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('authToken')
  }

  return { token, user, isLoading, error, login, logout }
})
