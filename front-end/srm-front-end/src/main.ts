import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useAuthStore } from './stores/authStore'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 應用程式啟動時，初始化 auth store 並嘗試從 localStorage 載入 token
const authStore = useAuthStore()
authStore.restoreSessionFromStorage()

app.mount('#app')
