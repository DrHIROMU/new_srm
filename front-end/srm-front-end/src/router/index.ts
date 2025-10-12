import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { useAuthStore } from '@/stores/authStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home', // 登入後預設進入 home
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/auth/callback',
      name: 'auth-callback',
      component: () => import('../views/AuthCallbackView.vue'),
    },
    {
      path: '/home',
      component: MainLayout,
      meta: { requiresAuth: true }, // 將 requiresAuth 移到父路由
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
        },
        {
          path: '/vendor-invitation',
          name: 'vendor-invitation',
          component: () => import('../views/vendor/VendorInvitationView.vue'),
        },
        {
          path: '/vendor-query',
          name: 'vendor-query',
          component: () => import('../views/vendor/VendorQueryView.vue'),
        },
        {
          path: '/vendor-change-process',
          name: 'vendor-change-process',
          component: () => import('../views/vendor/VendorChangeProcessView.vue'),
        },
        {
          path: '/vendor/new',
          name: 'new-vendor',
          component: () => import('../views/vendor/NewVendorView.vue'),
        },
      ],
    },
  ],
})

// 全域路由守衛
router.beforeEach((to, from, next) => {
  // 確保 Pinia store 已經被初始化
  const authStore = useAuthStore()

  // 每次路由變化時，都嘗試從 localStorage 載入 token
  // 這確保了使用者刷新頁面後登入狀態不會遺失
  if (!authStore.accessToken) {
    authStore.restoreSessionFromStorage()
  }

  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  // 如果目標頁面需要登入，但使用者未登入
  if (requiresAuth && !authStore.isAuthenticated) {
    // 將使用者導向登入頁
    next({ name: 'login' })
  }
  // 如果使用者已登入，但想進入登入頁，則直接導向主頁
  else if ((to.name === 'login' || to.name === 'auth-callback') && authStore.isAuthenticated) {
    next({ name: 'home' })
  }
  // 其他情況，正常放行
  else {
    next()
  }
})

export default router
