<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'
import { NSpin, NAlert, NCard } from 'naive-ui'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const error = ref<string | null>(null)

// onMounted is the ideal lifecycle hook to run this logic
onMounted(async () => {
  const code = route.query.code as string
  const state = route.query.state as string // Optional: for preventing CSRF

  // 1. Check if the 'code' parameter exists in the URL
  if (!code) {
    error.value = '授權碼遺失，無法完成登入。請重試。'
    // Redirect back to login after a delay
    setTimeout(() => router.push('/login'), 5000)
    return
  }

  // 2. Retrieve the code_verifier from sessionStorage
  const codeVerifier = sessionStorage.getItem('pkce_code_verifier')
  if (!codeVerifier) {
    error.value = '驗證資訊遺失，無法安全地完成登入。請重試。'
    setTimeout(() => router.push('/login'), 5000)
    return
  }

  // 3. Exchange the code for a token
  const success = await authStore.exchangeCodeForToken(code, codeVerifier)

  // 4. Clean up the verifier from session storage
  sessionStorage.removeItem('pkce_code_verifier')

  // 5. Redirect based on success or failure
  if (success) {
    // Redirect to the home page upon successful login
    router.push('/home')
  } else {
    // If the store has a specific error message, use it
    error.value = authStore.error || '發生未知錯誤，無法登入。'
    setTimeout(() => router.push('/login'), 5000)
  }
})
</script>

<template>
  <div class="flex justify-center items-center h-screen bg-gray-100">
    <n-card class="w-full max-w-md text-center">
      <template v-if="!error">
        <n-spin size="large" />
        <p class="mt-4 text-gray-600">正在完成登入，請稍候...</p>
      </template>
      <template v-else>
        <n-alert title="登入失敗" type="error">
          {{ error }}
          <p class="mt-2">將在 5 秒後將您導回登入頁面。</p>
        </n-alert>
      </template>
    </n-card>
  </div>
</template>
