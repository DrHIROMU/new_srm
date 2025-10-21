<script setup lang="ts">
import AiAssistantWidget from '@/components/ai/AiAssistantWidget.vue'
</script>

<template>
  <main class="home-view">
    <section class="home-hero">
      <h1 class="home-title">SRM 工作桌面</h1>
      <p class="home-subtitle">歡迎回來，立即開始管理您的採購流程。</p>
    </section>

    <AiAssistantWidget />
  </main>
</template>

<style scoped>
.home-view {
  position: relative;
  min-height: calc(100vh - 64px);
  padding: 48px 32px;
  background: linear-gradient(180deg, #f8fafc 0%, #ffffff 100%);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.home-hero {
  max-width: 720px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.home-title {
  font-size: 32px;
  font-weight: 700;
  color: #0f172a;
  margin: 0;
}

.home-subtitle {
  font-size: 16px;
  color: #475569;
  margin: 0;
}

@media (max-width: 768px) {
  .home-view {
    padding: 32px 20px;
  }

  .home-title {
    font-size: 24px;
  }
}
</style>
