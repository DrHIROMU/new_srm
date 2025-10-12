import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL

const apiClient = axios.create({
  baseURL: apiBaseUrl,
  timeout: 15000,
  withCredentials: true,
})

apiClient.interceptors.request.use((config) => {
  try {
    const authStore = useAuthStore()
    if (authStore.isAuthenticated && authStore.accessToken) {
      config.headers = config.headers ?? {}
      config.headers.Authorization = `Bearer ${authStore.accessToken}`
    }
  } catch (error) {
    console.warn('Unable to attach auth header', error)
  }
  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        const authStore = useAuthStore()
        await authStore.logout()
      } catch (logoutError) {
        console.warn('Failed to auto logout after 401', logoutError)
      }
    }
    return Promise.reject(error)
  },
)

export default apiClient
