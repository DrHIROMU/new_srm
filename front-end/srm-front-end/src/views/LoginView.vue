<script setup lang="ts">
import { ref, watchEffect } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const authStore = useAuthStore()
const route = useRoute()

const isRedirecting = ref(false)
const errorMessage = ref<string | null>(null)

const startOAuthRedirect = () => {
  isRedirecting.value = true
  errorMessage.value = null
  try {
    authStore.startLogin()
  } catch (error) {
    console.error('Failed to initiate OAuth redirect', error)
    errorMessage.value = '啟動登入流程時發生問題，請稍後再試。'
    isRedirecting.value = false
  }
}

watchEffect(() => {
  if (route.query.error) {
    errorMessage.value = '登入失敗，請重新操作。'
  } else if (authStore.error) {
    errorMessage.value = authStore.error
  } else {
    errorMessage.value = null
  }

  if (errorMessage.value) {
    isRedirecting.value = false
  }
})
</script>

<template>
  <section class="login">
    <div class="login__hero">
      <div class="login__brand">Advantech SRM</div>
      <h1 class="login__headline">供應商資訊管理入口</h1>
      <p class="login__intro">
        集中管理供應商資料、資格審核與協作流程，協助您快速完成註冊、邀請與維護。
      </p>
      <ul class="login__features">
        <li>即時掌握邀請與審核進度</li>
        <li>支援文件更新與版本追蹤</li>
        <li>整合採購、合約與供應商表現</li>
      </ul>
    </div>
    <div class="login__card">
      <div class="login__card-header">
        <h2>登入 SRM 平台</h2>
        <p>使用 Advantech 員工或供應商帳號登入，系統會依 email 自動分流驗證機制。</p>
      </div>
      <button class="login__button" :disabled="isRedirecting" @click="startOAuthRedirect">
        {{ isRedirecting ? '前往登入中…' : '前往登入' }}
      </button>
      <p v-if="errorMessage" class="login__error">{{ errorMessage }}</p>
      <div class="login__meta">
        <p>
          需要協助？請洽 SRM 管理窗口
          <a href="mailto:srm-support@advantech.com">srm-support@advantech.com</a>
        </p>
        <p class="login__hint">登入按鈕將導向驗證中心頁面，完成驗證後會自動返回系統。</p>
      </div>
    </div>
  </section>
</template>

<style scoped>
.login {
  min-height: 100vh;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 3rem;
  align-items: center;
  padding: 4rem clamp(2rem, 5vw, 6rem);
  background: radial-gradient(circle at 10% 20%, rgba(62, 134, 255, 0.18) 0%, transparent 55%),
    radial-gradient(circle at 90% 15%, rgba(26, 76, 203, 0.18) 0%, transparent 50%),
    linear-gradient(135deg, #f0f4ff, #ffffff 65%);
  box-sizing: border-box;
}

.login__hero {
  max-width: 520px;
  color: #1f2d3d;
}

.login__brand {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.4rem;
  border-radius: 999px;
  background: rgba(15, 98, 254, 0.1);
  color: #0f62fe;
  font-weight: 600;
  letter-spacing: 0.04em;
  text-transform: uppercase;
}

.login__headline {
  margin: 1.5rem 0 1rem;
  font-size: clamp(2rem, 4vw, 2.8rem);
  line-height: 1.25;
}

.login__intro {
  margin: 0 0 1.5rem;
  font-size: 1rem;
  color: #52667a;
  line-height: 1.6;
}

.login__features {
  margin: 0;
  padding-left: 1.2rem;
  color: #32445a;
  display: grid;
  gap: 0.5rem;
}

.login__features li {
  line-height: 1.5;
}

.login__card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 24px 60px rgba(15, 98, 254, 0.12);
  padding: clamp(2.5rem, 5vw, 3.5rem);
  max-width: 420px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  border: 1px solid rgba(15, 98, 254, 0.08);
}

.login__card-header h2 {
  margin: 0;
  font-size: 1.6rem;
  color: #0b1f44;
}

.login__card-header p {
  margin: 0.75rem 0 0;
  color: #54657a;
  line-height: 1.6;
}

.login__button {
  background: linear-gradient(135deg, #0f62fe 0%, #0043ce 100%);
  color: #ffffff;
  border: none;
  border-radius: 14px;
  padding: 0.95rem 1.5rem;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.15s ease, box-shadow 0.2s ease, opacity 0.2s ease;
  box-shadow: 0 14px 32px rgba(15, 98, 254, 0.25);
}

.login__button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 18px 36px rgba(15, 98, 254, 0.28);
}

.login__button:disabled {
  opacity: 0.7;
  cursor: progress;
  box-shadow: none;
}

.login__error {
  margin: -0.5rem 0 0;
  padding: 0.75rem 1rem;
  border-radius: 12px;
  background: rgba(218, 30, 40, 0.08);
  color: #b01921;
  font-size: 0.95rem;
  line-height: 1.4;
}

.login__meta {
  font-size: 0.9rem;
  color: #6b7c90;
  line-height: 1.5;
  display: grid;
  gap: 0.5rem;
}

.login__meta a {
  color: #0f62fe;
  font-weight: 600;
  text-decoration: none;
}

.login__meta a:hover {
  text-decoration: underline;
}

.login__hint {
  font-size: 0.85rem;
  color: #8a9bb2;
}

@media (max-width: 960px) {
  .login {
    padding: clamp(2rem, 8vw, 4rem);
    text-align: center;
  }

  .login__hero {
    margin: 0 auto;
  }

  .login__features {
    justify-items: center;
    padding-left: 0;
    list-style: none;
  }
}
</style>
