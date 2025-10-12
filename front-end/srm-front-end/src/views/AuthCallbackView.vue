<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { consumeCodeVerifier, consumeState, getCodeVerifier, getState } from '@/utils/oauthStorage'

const statusMessage = ref('驗證中，請稍候...')
const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const redirectToLogin = async (reason: string) => {
  await router.replace({ name: 'login', query: { error: reason } })
}

onMounted(async () => {
  const code = typeof route.query.code === 'string' ? route.query.code : null
  const state = typeof route.query.state === 'string' ? route.query.state : null

  const storedState = getState()
  if (!code || !state || !storedState || state !== storedState) {
    statusMessage.value = '授權資訊驗證失敗，請重新登入'
    consumeCodeVerifier()
    consumeState()
    await redirectToLogin('invalid_state')
    return
  }

  const codeVerifier = getCodeVerifier()
  consumeState()

  if (!codeVerifier) {
    statusMessage.value = '授權資訊已失效，請重新登入'
    consumeCodeVerifier()
    await redirectToLogin('missing_verifier')
    return
  }

  try {
    const success = await authStore.exchangeCodeForToken(code, codeVerifier)
    consumeCodeVerifier()
    if (!success) {
      statusMessage.value = authStore.error ?? '交換授權碼失敗，請重新登入'
      await redirectToLogin('token_exchange_failed')
      return
    }
    await authStore.fetchProfile()
    statusMessage.value = '登入成功，即將導向首頁'
    await router.replace({ name: 'home' })
  } catch (error) {
    console.error('OAuth callback handling failed', error)
    statusMessage.value = '登入流程發生錯誤，請重新嘗試'
    await redirectToLogin('callback_error')
  }
})
</script>

<template>
  <section class="callback">
    <div class="callback__card">
      <h1 class="callback__title">處理登入中</h1>
      <p class="callback__status">{{ statusMessage }}</p>
    </div>
  </section>
</template>

<style scoped>
.callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: #f2f4f8;
}

.callback__card {
  background-color: #ffffff;
  padding: 36px;
  border-radius: 12px;
  box-shadow: 0 16px 32px rgba(0, 0, 0, 0.08);
  text-align: center;
  width: min(90%, 360px);
}

.callback__title {
  margin: 0 0 12px;
  font-size: 24px;
  color: #001141;
}

.callback__status {
  margin: 0;
  color: #4d5358;
  font-size: 16px;
  line-height: 1.5;
}
</style>
