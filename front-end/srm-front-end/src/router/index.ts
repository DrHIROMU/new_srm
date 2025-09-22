import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/login'
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue')
    },
    {
      path: '/home',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('../views/HomeView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/vendor-invitation',
          name: 'vendor-invitation',
          component: () => import('../views/vendor/VendorInvitationView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/vendor-query',
          name: 'vendor-query',
          component: () => import('../views/vendor/VendorQueryView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/vendor-change-process',
          name: 'vendor-change-process',
          component: () => import('../views/vendor/VendorChangeProcessView.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: '/vendor/new',
          name: 'new-vendor',
          component: () => import('../views/vendor/NewVendorView.vue'),
          meta: { requiresAuth: true }
        }
      ]
    }
  ]
})

export default router
