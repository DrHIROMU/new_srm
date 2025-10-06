<script setup lang="ts">
import { ref } from 'vue'
import { NButton, NCard } from 'naive-ui'

const isLoading = ref(false)

/**
 * Generates a secure random string for the PKCE code_verifier.
 * @param length The length of the string to generate.
 */
function generateRandomString(length: number): string {
  const array = new Uint32Array(length / 2)
  window.crypto.getRandomValues(array)
  return Array.from(array, (dec) => ('0' + dec.toString(16)).slice(-2)).join('')
}

/**
 * Hashes the code_verifier using SHA-256 and then Base64URL encodes it to create the code_challenge.
 * @param verifier The code_verifier string.
 */
async function generateCodeChallenge(verifier: string): Promise<string> {
  const encoder = new TextEncoder()
  const data = encoder.encode(verifier)
  const digest = await window.crypto.subtle.digest('SHA-256', data)

  // Base64URL encoding
  return btoa(String.fromCharCode(...new Uint8Array(digest)))
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '')
}

const handleOAuthLogin = async () => {
  isLoading.value = true

  // --- [重要] 請根據您的 Auth Server 設定修改以下變數 ---
  // 您的 Spring Authorization Server 基礎 URL
  const authServerUrl = 'http://localhost:8081' 
  // 您前端在 Auth Server 註冊的 Client ID
  const clientId = 'srm-frontend' 
  // 授權成功後，Auth Server 要跳轉回來的完整 URL (需與 Auth Server 設定一致)
  const redirectUri = 'http://localhost:5173/login/callback' 
  // 您想申請的權限，對於 OIDC 至少需要 'openid'
  const scopes = 'openid profile' 
  // ----------------------------------------------------

  try {
    // 1. 產生並儲存 PKCE code_verifier
    const codeVerifier = generateRandomString(128)
    // 使用 sessionStorage，關閉分頁後自動清除
    sessionStorage.setItem('pkce_code_verifier', codeVerifier)

    // 2. 產生 code_challenge
    const codeChallenge = await generateCodeChallenge(codeVerifier)

    // 3. 組合授權 URL
    const params = new URLSearchParams({
      response_type: 'code',
      client_id: clientId,
      redirect_uri: redirectUri,
      scope: scopes,
      code_challenge: codeChallenge,
      code_challenge_method: 'S256',
    })

    const authorizationUrl = `${authServerUrl}/oauth2/authorize?${params.toString()}`

    // 4. 重新導向到 Auth Server 進行登入和授權
    window.location.href = authorizationUrl
  } catch (error) {
    console.error('OAuth 登入流程啟動失敗:', error)
    isLoading.value = false
    // 在此可以加入錯誤提示，例如使用 naive-ui 的 message
  }
}
</script>

<template>
  <div class="flex justify-center items-center h-screen bg-gray-100">
    <n-card class="w-full max-w-sm" title="系統登入">
      <div class="text-center">
        <p class="mb-6 text-gray-600">
          歡迎使用供應商關係管理系統，請點擊下方按鈕進行身分驗證。
        </p>
        <n-button
          type="primary"
          class="w-full"
          @click="handleOAuthLogin"
          :loading="isLoading"
          size="large"
        >
          使用企業帳號登入
        </n-button>
      </div>
    </n-card>
  </div>
</template>