<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const statusMessage = ref('驗證中，請稍候...')
const authStore = useAuthStore()
const route = useRoute()
const router = useRouter()

const redirectToLogin = async (reason: string) => {
  await router.replace({ name: 'login', query: { error: reason } })
}

onMounted(async () => {
  try {
    const authenticated = await authStore.refreshSession()
    if (authenticated) {
      statusMessage.value = '登入成功，即將導向首頁'
      await router.replace({ name: 'home' })
      return
    }
    statusMessage.value = '尚未登入，請重新嘗試'
    await redirectToLogin(typeof route.query.error === 'string' ? route.query.error : 'unauthenticated')
  } catch (error) {
    console.error('Failed to establish session after callback', error)
    statusMessage.value = authStore.error ?? '登入流程發生錯誤，請重新嘗試'
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
