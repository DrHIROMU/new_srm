<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { buildAuthorizeUrl } from '@/services/authService'
import { createPkcePair } from '@/utils/pkce'
import { saveCodeVerifier, saveState } from '@/utils/oauthStorage'

const isRedirecting = ref(false)
const errorMessage = ref<string | null>(null)

const route = useRoute()

const generateState = (length = 32): string => {
  const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789'
  const randomValues = new Uint32Array(length)
  window.crypto.getRandomValues(randomValues)

  let state = ''
  for (let i = 0; i < length; i += 1) {
    state += charset.charAt(randomValues[i] % charset.length)
  }
  return state
}

const startOAuthRedirect = async () => {
  isRedirecting.value = true
  errorMessage.value = null
  try {
    const { codeVerifier, codeChallenge } = await createPkcePair()
    const state = generateState()
    saveCodeVerifier(codeVerifier)
    saveState(state)
    const authorizeUrl = buildAuthorizeUrl({ codeChallenge, state })
    window.location.assign(authorizeUrl)
  } catch (error) {
    console.error('Failed to initiate OAuth redirect', error)
    errorMessage.value = '初始化登入流程失敗，請稍後再試'
    isRedirecting.value = false
  }
}

watchEffect(() => {
  if (route.query.error) {
    errorMessage.value = '登入失敗，請重新嘗試'
  }
})
</script>

<template>
  <section class="login">
    <div class="login__card">
      <h1 class="login__title">SRM 登入</h1>
      <p class="login__subtitle">請使用 Advantech 或供應商帳號進行驗證</p>
      <button class="login__button" :disabled="isRedirecting" @click="startOAuthRedirect">
        {{ isRedirecting ? '前往驗證中...' : '使用 OAuth2 登入' }}
      </button>
      <p v-if="errorMessage" class="login__error">{{ errorMessage }}</p>
    </div>
  </section>
</template>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f62fe 0%, #0043ce 100%);
}

.login__card {
  background-color: #ffffff;
  padding: 48px;
  border-radius: 12px;
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.15);
  text-align: center;
  width: min(90%, 420px);
}

.login__title {
  margin: 0 0 8px;
  font-size: 32px;
  color: #001141;
}

.login__subtitle {
  margin: 0 0 24px;
  color: #6f6f6f;
  font-size: 16px;
}

.login__button {
  background-color: #0f62fe;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 16px;
  cursor: pointer;
  width: 100%;
  transition: background-color 0.2s ease;
}

.login__button:disabled {
  background-color: #8ab4f8;
  cursor: progress;
}

.login__button:not(:disabled):hover {
  background-color: #0043ce;
}

.login__error {
  margin-top: 16px;
  color: #da1e28;
  font-size: 14px;
}
</style>
