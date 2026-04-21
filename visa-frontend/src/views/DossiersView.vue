<script setup>
import { ref, onMounted } from 'vue'
import SideNav from '../components/SideNav.vue'
import { getDossiers } from '../services/api.js'

const dossiers = ref([])
const loading = ref(false)
const errorMsg = ref('')

function formatDateTime(value) {
  if (!value) return ''
  const dt = new Date(value)
  if (Number.isNaN(dt.getTime())) return String(value)
  return new Intl.DateTimeFormat('fr-FR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  }).format(dt)
}

function statusClass(statut) {
  const s = (statut || '').toUpperCase()
  if (s === 'VALIDE' || s === 'VALIDÉ' || s === 'VALIDEE') return 'badge badge--primary'
  if (s === 'ANNULE' || s === 'ANNULÉ' || s === 'ANNULEE') return 'badge badge--danger'
  return 'badge badge--muted'
}

async function load() {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await getDossiers()
    dossiers.value = Array.isArray(data) ? data : []
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger les dossiers'
    dossiers.value = []
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
          <h1>Dossiers</h1>
          <p>Tableau de tous les dossiers</p>
        </div>
        <div class="header-right">
          <router-link to="/dossier/nouveau" class="btn-create">Nouveau Dossier</router-link>
        </div>
      </header>

      <main class="canvas">
        <div v-if="errorMsg" class="error-msg-box">{{ errorMsg }}</div>
        <div v-else-if="loading" class="state-msg">Chargement…</div>

        <div v-else class="paper">
          <table class="table">
            <thead>
              <tr>
                <th>Référence</th>
                <th>Demandeur</th>
                <th>Type visa</th>
                <th>Type demande</th>
                <th>Statut</th>
                <th>Créé le</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="d in dossiers" :key="d.id">
                <td class="mono">{{ d.numDemande }}</td>
                <td>{{ (d.nom || '') + (d.prenoms ? ' ' + d.prenoms : '') }}</td>
                <td>{{ d.typeVisa }}</td>
                <td>{{ d.typeDemande }}</td>
                <td><span :class="statusClass(d.statut)">{{ (d.statut || '').toUpperCase() }}</span></td>
                <td>{{ formatDateTime(d.dateCreation) }}</td>
                <td class="actions">
                  <router-link class="link" :to="`/dossiers/${d.id}`">Voir</router-link>
                </td>
              </tr>
              <tr v-if="dossiers.length === 0">
                <td colspan="7" class="empty">Aucun dossier.</td>
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

.btn-create {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-blue);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  font-family: 'Inter', sans-serif;
  transition: opacity 0.15s;
}

.btn-create:hover { opacity: 0.88; }

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

.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; }

.actions { text-align: right; }

.link {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 600;
}

.link:hover { text-decoration: underline; }

.badge {
  display: inline-flex;
  align-items: center;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid var(--border-color);
  font-size: 12px;
  font-weight: 700;
}

.badge--muted { color: var(--text-gray); }
.badge--primary { color: var(--primary-blue); border-color: rgba(21, 112, 176, 0.35); }
.badge--danger { color: var(--danger-red); border-color: rgba(211, 47, 47, 0.35); }

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
