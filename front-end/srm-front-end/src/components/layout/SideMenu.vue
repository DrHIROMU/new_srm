<script setup lang="ts">
import { ref } from 'vue'

defineProps<{
  isCollapsed: boolean
}>()

const isVendorMenuOpen = ref(false)

function toggleVendorMenu() {
  isVendorMenuOpen.value = !isVendorMenuOpen.value
}
</script>

<template>
  <aside class="side-menu" :class="{ collapsed: isCollapsed }">
    <nav>
      <ul>
        <li><router-link to="/home">首頁</router-link></li>
        <li class="menu-item">
          <a @click="toggleVendorMenu" :class="{ 'active': isVendorMenuOpen }">
            供應商管理
            <span class="arrow" :class="{ 'down': isVendorMenuOpen }"></span>
          </a>
          <ul v-if="isVendorMenuOpen" class="sub-menu">
            <li><router-link to="/vendor-invitation">邀請供應商</router-link></li>
            <li><router-link to="/vendor-query">供應商查詢</router-link></li>
            <li><router-link to="/vendor-change-process">供應商資料變更流程</router-link></li>
          </ul>
        </li>
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
  cursor: pointer;
}

.side-menu nav li a:hover,
.side-menu nav li a.router-link-exact-active {
  background-color: #e9ecef;
}

.menu-item a {
  position: relative;
}

.arrow {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #333;
  transition: transform 0.3s ease;
}

.arrow.down {
  transform: translateY(-50%) rotate(180deg);
}

.sub-menu {
  padding-left: 20px;
}

.sub-menu li a {
  padding: 8px 20px;
  font-size: 14px;
}
</style>
