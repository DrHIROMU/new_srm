<script setup lang="ts">
import {
  NButton,
  NCard,
  NForm,
  NFormItem,
  NInput,
  useMessage,
  type FormInst,
  type FormRules,
} from 'naive-ui'
import { ref } from 'vue'
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import type { LoginCredentials } from '@/types/auth'

const formRef = ref<FormInst | null>(null)
const model = ref<LoginCredentials>({
  email: '',
  password: '',
})

const authStore = useAuthStore()
const router = useRouter()
const message = useMessage()

const rules: FormRules = {
  email: {
    required: true,
    type: 'email',
    message: '請輸入正確的E-mail',
    trigger: ['blur', 'input'],
  },
  password: {
    required: true,
    message: '請輸入密碼',
    trigger: ['blur', 'input'],
  },
}

const handleLogin = (e: MouseEvent) => {
  e.preventDefault()
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      const success = await authStore.login(model.value)
      if (success) {
        message.success('登入成功')
        router.push('/home')
      } else {
        message.error(authStore.error || '登入失敗')
      }
    } else {
      message.error('請檢查輸入的內容')
    }
  })
}
</script>

<template>
  <div class="flex justify-center items-center h-screen bg-gray-100">
    <n-card class="w-full max-w-sm" title="登入">
      <n-form ref="formRef" :model="model" :rules="rules">
        <n-form-item path="email" label="E-mail">
          <n-input
            type="text"
            v-model:value="model.email"
            placeholder="請輸入E-mail"
            :disabled="authStore.isLoading"
          />
        </n-form-item>
        <n-form-item path="password" label="密碼">
          <n-input
            v-model:value="model.password"
            type="password"
            show-password-on="mousedown"
            placeholder="請輸入密碼"
            :disabled="authStore.isLoading"
          />
        </n-form-item>
        <n-form-item>
          <n-button
            type="primary"
            class="w-full"
            @click="handleLogin"
            :loading="authStore.isLoading"
          >
            登入
          </n-button>
        </n-form-item>
      </n-form>
    </n-card>
  </div>
</template>
