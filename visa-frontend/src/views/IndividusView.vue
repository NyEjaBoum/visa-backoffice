<script setup>
import { ref, onMounted } from 'vue'
import SideNav from '../components/SideNav.vue'
import { getIndividusComplets } from '../services/api.js'

const individus = ref([])
const loading = ref(false)
const errorMsg = ref('')

function todayIso() {
  const now = new Date()
  const y = now.getFullYear()
  const m = String(now.getMonth() + 1).padStart(2, '0')
  const d = String(now.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

function isExpiredDate(localDateStr) {
  if (!localDateStr) return false
  if (typeof localDateStr !== 'string') return false
  if (!/^\d{4}-\d{2}-\d{2}$/.test(localDateStr)) return false
  return localDateStr < todayIso()
}

function formatDate(value) {
  if (!value) return ''
  if (typeof value === 'string' && /^\d{4}-\d{2}-\d{2}$/.test(value)) {
    const [y, m, d] = value.split('-')
    return `${d}/${m}/${y}`
  }
  const dt = new Date(value)
  if (Number.isNaN(dt.getTime())) return String(value)
  return new Intl.DateTimeFormat('fr-FR', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(dt)
}

function initials(ind) {
  const nom = (ind?.nom || '').trim()
  const prenom = (ind?.prenom || '').trim()
  const first = (prenom || nom || '?').charAt(0)
  return first ? first.toUpperCase() : '?'
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await getIndividusComplets()
    individus.value = Array.isArray(data) ? data : []
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger les individus'
    individus.value = []
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="app-shell">
    <SideNav />

    <div class="page-wrapper">
      <header class="page-header">
        <div class="page-title">
          <h1>Individus</h1>
          <p>Tableau des individus</p>
        </div>
      </header>

      <main class="canvas">
        <div v-if="errorMsg" class="error-msg-box">{{ errorMsg }}</div>
        <div v-else-if="loading" class="state-msg">Chargement…</div>

        <div v-else class="paper">
          <table class="table">
            <thead>
              <tr>
                <th></th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Passeport</th>
                <th>Date d'expiration</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="i in individus" :key="i.individuId">
                <td class="avatar-cell">
                  <div class="avatar">{{ initials(i) }}</div>
                </td>
                <td>{{ i.nom }}</td>
                <td>{{ i.prenom }}</td>
                <td class="mono">{{ i.passeportNumero }}</td>
                <td :class="{ expired: isExpiredDate(i.dateExpiration) }">{{ formatDate(i.dateExpiration) }}</td>
                <td class="actions">
                  <router-link class="link" :to="`/individus/${i.individuId}`">Voir</router-link>
                </td>
              </tr>
              <tr v-if="individus.length === 0">
                <td colspan="6" class="empty">Aucun individu.</td>
              </tr>
            </tbody>
          </table>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  height: 100vh;
  background: #f1f3f6;
  overflow: hidden;
}

.page-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.page-header {
  background: white;
  padding: 12px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
}

.page-title h1 {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-dark);
}

.page-title p {
  font-size: 12px;
  color: var(--text-gray);
  margin-top: 1px;
}

.canvas {
  flex: 1;
  padding: 24px 28px;
  overflow-y: auto;
}

.paper {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  text-align: left;
  font-size: 12px;
  color: var(--text-gray);
  font-weight: 600;
  padding: 12px 14px;
  background: #fafafa;
  border-bottom: 1px solid #eef2f7;
}

.table td {
  padding: 12px 14px;
  border-bottom: 1px solid #eef2f7;
  font-size: 13px;
  color: var(--text-dark);
  vertical-align: middle;
}

.table tr:hover td { background: #fbfdff; }

.avatar-cell { width: 48px; }

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 999px;
  border: 1px solid var(--border-color);
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  color: var(--primary-blue);
}

.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; }

.actions { text-align: right; }

.link {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 600;
}

.link:hover { text-decoration: underline; }

.expired { color: var(--danger-red); font-weight: 700; }

.state-msg {
  font-size: 13px;
  color: var(--text-gray);
}

.error-msg-box {
  background: rgba(211, 47, 47, 0.06);
  border: 1px solid rgba(211, 47, 47, 0.20);
  color: var(--danger-red);
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 14px;
}

.empty {
  text-align: center;
  color: var(--text-gray);
  padding: 18px 14px;
}
</style>
