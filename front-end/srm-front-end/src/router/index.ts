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
router.beforeEach(async (to) => {
  const authStore = useAuthStore()
  await authStore.initializeSession()

  const requiresAuth = to.matched.some((record) => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated) {
    return { name: 'login' }
  }

  if ((to.name === 'login' || to.name === 'auth-callback') && authStore.isAuthenticated) {
    return { name: 'home' }
  }

  return true
})

export default router
