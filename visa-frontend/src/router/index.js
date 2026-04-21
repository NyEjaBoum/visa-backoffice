import { createRouter, createWebHistory } from 'vue-router'
import LoginView from '../views/LoginView.vue'
import DashboardView from '../views/DashboardView.vue'
import DossierFormView from '../views/DossierFormView.vue'

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: LoginView },
  { path: '/dashboard', component: DashboardView, meta: { requiresAuth: true } },
  { path: '/dossier/nouveau', component: DossierFormView, meta: { requiresAuth: true } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const isAuthenticated = !!localStorage.getItem('token')
  if (to.path === '/login' && isAuthenticated) {
    next('/dashboard')
  } else if (to.meta.requiresAuth && !isAuthenticated) {
    // Si la page nécessite auth et pas de token, va au login
    next('/login');
  } else {
    next();
  }
})

export default router
