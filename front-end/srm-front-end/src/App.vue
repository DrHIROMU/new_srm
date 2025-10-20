<template>
  <RouterView />
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
</script>

<style>
html,
body,
#app {
  height: 100%;
}

body {
  margin: 0;
  font-family: 'Inter', 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif;
  background: #f4f6fb;
  color: #1f2d3d;
}
</style>
