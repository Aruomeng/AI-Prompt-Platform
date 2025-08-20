import { createRouter, createWebHistory } from 'vue-router'
import Cookies from 'js-cookie'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/prompts',
    name: 'PromptList',
    component: () => import('../views/PromptList.vue')
  },
  {
    path: '/prompts/:id',
    name: 'PromptDetail',
    component: () => import('../views/PromptDetail.vue'),
    props: true
  },
  {
    path: '/create-prompt',
    name: 'CreatePrompt',
    component: () => import('../views/CreatePrompt.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/prompts/:id/edit',
    name: 'EditPrompt',
    component: () => import('../views/EditPrompt.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-prompts',
    name: 'MyPrompts',
    component: () => import('../views/MyPrompts.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/my-favorites',
    name: 'MyFavorites',
    component: () => import('../views/MyFavorites.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    redirect: '/admin/prompt-audit',
    children: [
      {
        path: 'prompt-audit',
        name: 'PromptAudit',
        component: () => import('../views/admin/PromptAudit.vue')
      },
      {
        path: 'prompt-management',
        name: 'PromptManagement',
        component: () => import('../views/admin/PromptManagement.vue')
      },
      {
        path: 'comment-management',
        name: 'CommentManagement',
        component: () => import('../views/admin/CommentManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = Cookies.get('token')
  const userInfo = Cookies.get('userInfo') ? JSON.parse(Cookies.get('userInfo')) : null
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.meta.requiresAdmin && (!token || !userInfo || (userInfo.role !== 'ADMIN' && userInfo.role !== 'admin'))) {
    next('/')
  } else {
    next()
  }
})

export default router