<script setup lang="ts">
defineProps<{
  isCollapsed: boolean
}>()
</script>

<template>
  <aside class="side-menu" :class="{ collapsed: isCollapsed }">
    <nav>
      <ul>
        <li><router-link to="/home">首頁</router-link></li>
        <!-- Add other menu items here -->
        <li><a href="#">功能一</a></li>
        <li><a href="#">功能二</a></li>
      </ul>
    </nav>
  </aside>
</template>

<style scoped>
.side-menu {
  width: 200px;
  flex-shrink: 0;
  background-color: #f8f9fa;
  border-right: 1px solid #e0e0e0;
  transition: width 0.3s ease;
  overflow-y: auto;
}

.side-menu.collapsed {
  width: 0;
  overflow: hidden;
}

.side-menu nav ul {
  list-style: none;
  padding: 20px 0;
  margin: 0;
}

.side-menu nav li a {
  display: block;
  padding: 10px 20px;
  text-decoration: none;
  color: #333;
}

.side-menu nav li a:hover,
.side-menu nav li a.router-link-exact-active {
  background-color: #e9ecef;
}
</style>
