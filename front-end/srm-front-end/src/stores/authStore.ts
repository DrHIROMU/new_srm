import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'
import type { User } from '@/types/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  // TODO: 之後可以透過解析 id_token 來取得使用者資訊
  const user = ref<User | null>(null) 
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!token.value)

  /**
   * 在應用程式啟動時嘗試從 localStorage 載入 token
   */
  function tryLoadTokenFromStorage() {
    const storedToken = localStorage.getItem('authToken')
    if (storedToken) {
      token.value = storedToken
      // 在此可以加入驗證 token 是否過期的邏輯
    }
  }

  /**
   * 使用授權碼 (code) 和 codeVerifier 交換 access_token
   * @param code 從 Auth Server redirect 回來的授權碼
   * @param codeVerifier PKCE 流程中儲存在 sessionStorage 的驗證碼
   */
  async function exchangeCodeForToken(code: string, codeVerifier: string): Promise<boolean> {
    isLoading.value = true
    error.value = null

    // --- [重要] 請根據您的 Auth Server 設定修改以下變數 ---
    const tokenUrl = 'http://localhost:8081/oauth2/token'
    const clientId = 'srm-frontend'
    const redirectUri = 'http://localhost:5173/login/callback'
    // ----------------------------------------------------

    const params = new URLSearchParams()
    params.append('grant_type', 'authorization_code')
    params.append('code', code)
    params.append('redirect_uri', redirectUri)
    params.append('client_id', clientId)
    params.append('code_verifier', codeVerifier)

    try {
      const response = await axios.post(tokenUrl, params, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      })

      token.value = response.data.access_token
      localStorage.setItem('authToken', token.value)
      
      // 可選：如果 Auth Server 回傳了 id_token，可以在此解析並儲存使用者資訊
      // const idToken = response.data.id_token
      // if (idToken) {
      //   // 解析 JWT (需要 jwt-decode 之類的庫)
      // }

      return true
    } catch (e: any) {
      console.error('Token exchange failed:', e)
      error.value = e.response?.data?.error_description || '權杖交換失敗，請重新登入'
      return false
    } finally {
      isLoading.value = false
    }
  }

  /**
   * 登出，清除 token 和使用者資訊
   */
  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('authToken')
    // 可選：如果需要，可以將使用者導向到 Auth Server 的登出端點
    // window.location.href = 'http://localhost:9000/logout'
  }

  return {
    token,
    user,
    isLoading,
    error,
    isAuthenticated,
    tryLoadTokenFromStorage,
    exchangeCodeForToken,
    logout,
  }
})
