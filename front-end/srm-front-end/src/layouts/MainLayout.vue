<script setup lang="ts">
import { ref } from 'vue'
import { RouterView } from 'vue-router'
import TopBanner from '@/components/layout/TopBanner.vue'
import SideMenu from '@/components/layout/SideMenu.vue'

const isSidebarCollapsed = ref(false)

function toggleSidebar() {
  isSidebarCollapsed.value = !isSidebarCollapsed.value
}
</script>

<template>
  <div class="main-layout">
    <TopBanner @toggle-sidebar="toggleSidebar" />

    <div class="content-wrapper">
      <SideMenu :is-collapsed="isSidebarCollapsed" />
      <main class="main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.main-layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  overflow: hidden;
}

.content-wrapper {
  display: flex;
  flex-grow: 1;
  overflow: hidden;
}

.main-content {
  flex-grow: 1;
  padding: 20px;
  overflow-y: auto;
  background-color: #f0f2f5;
}
</style>

