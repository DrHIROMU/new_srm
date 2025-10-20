import axios from 'axios'
import { useAuthStore } from '@/stores/authStore'

const bffBaseUrl = import.meta.env.VITE_BFF_BASE_URL || '/bff'

const apiClient = axios.create({
  baseURL: bffBaseUrl,
  timeout: 15000,
  withCredentials: true,
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      try {
        const authStore = useAuthStore()
        authStore.clearSession()
      } catch (storeError) {
        console.warn('Failed to clear session after 401', storeError)
      }
    }
    return Promise.reject(error)
  },
)

export default apiClient
