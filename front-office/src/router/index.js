import { createRouter, createWebHashHistory } from 'vue-router'
import SuiviView from '../views/SuiviView.vue'

const routes = [
  { path: '/', redirect: '/suivi' },
  { path: '/suivi', component: SuiviView },
  { path: '/suivi/:token', component: SuiviView, props: true }
]

export default createRouter({
  history: createWebHashHistory(),
  routes
})
