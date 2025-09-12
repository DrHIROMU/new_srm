<script setup lang="ts">
import { useAuthStore } from '@/stores/authStore'
import { useRouter } from 'vue-router'
import { NIcon } from 'naive-ui'
import {Menu} from '@vicons/ionicons5'
import { Person24Regular } from '@vicons/fluent'

const authStore = useAuthStore()
const router = useRouter()

const emit = defineEmits(['toggle-sidebar'])

function toggleSidebar() {
  emit('toggle-sidebar')
}

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<template>
  <header class="top-banner">
    <div class="banner-left">
      <button @click="toggleSidebar" class="sidebar-toggle">
        <n-icon size="24">
          <Menu />
        </n-icon>
      </button>
    </div>
    <div class="banner-right">
      <button @click="handleLogout" class="logout-button">登出</button>
      <div class="user-avatar">
        <n-icon size="24">
          <Person24Regular />
        </n-icon>
      </div>
    </div>
  </header>
</template>

<style scoped>
.top-banner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e0e0e0;
  flex-shrink: 0;
}

.banner-left,
.banner-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.sidebar-toggle {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
  display: flex;
  align-items: center;
}

.logout-button {
  background-color: #f0f2f5;
  border: 1px solid #d9d9d9;
  padding: 5px 15px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.logout-button:hover {
  background-color: #e6e6e6;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #ccc;
  display: flex;
  justify-content: center;
  align-items: center;
  color: #fff;
}
</style>
