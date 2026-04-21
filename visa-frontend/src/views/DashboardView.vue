<script setup>
import { ref, onMounted } from 'vue'
import { getDashboardStats } from '../services/api.js'
import SideNav from '../components/SideNav.vue'

const stats = ref(null)
const statsError = ref(false)

onMounted(async () => {
  try {
    stats.value = await getDashboardStats()
  } catch {
    statsError.value = true
  }
})
</script>

<template>
  <div class="app-shell">
    <SideNav />

    <div class="page-wrapper">

      <!-- Header bar (style form.html) -->
      <header class="page-header">
        <div class="header-left">
          <div class="page-title">
            <h1>Tableau de bord</h1>
            <p>Vue d'ensemble des dossiers de visa</p>
          </div>
        </div>
        <div class="header-right">
          <router-link to="/dossier/nouveau" class="btn-create">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
            Nouveau Dossier
          </router-link>
        </div>
      </header>

      <!-- Canvas -->
      <main class="canvas">

        <div v-if="stats" class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon total">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14,2 14,8 20,8"/>
              </svg>
            </div>
            <div class="stat-body">
              <span class="stat-label">Total dossiers</span>
              <span class="stat-value">{{ stats.total }}</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon created">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <polyline points="12,6 12,12 16,14"/>
              </svg>
            </div>
            <div class="stat-body">
              <span class="stat-label">Créés</span>
              <span class="stat-value">{{ stats.crees }}</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon validated">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="20,6 9,17 4,12"/>
              </svg>
            </div>
            <div class="stat-body">
              <span class="stat-label">Validés</span>
              <span class="stat-value">{{ stats.valides }}</span>
            </div>
          </div>

          <div class="stat-card">
            <div class="stat-icon cancelled">
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <circle cx="12" cy="12" r="10"/>
                <line x1="15" y1="9" x2="9" y2="15"/>
                <line x1="9" y1="9" x2="15" y2="15"/>
              </svg>
            </div>
            <div class="stat-body">
              <span class="stat-label">Annulés</span>
              <span class="stat-value">{{ stats.annules }}</span>
            </div>
          </div>
        </div>

        <div v-else-if="!statsError" class="state-msg">Chargement des statistiques…</div>
        <div v-else class="error-msg-box">Impossible de charger les statistiques.</div>

        <!-- Encart vide -->
        <div class="empty-paper">
          <svg width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" stroke-width="1.5">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
            <polyline points="14,2 14,8 20,8"/>
            <line x1="12" y1="18" x2="12" y2="12"/>
            <line x1="9" y1="15" x2="15" y2="15"/>
          </svg>
          <p>Créez un nouveau dossier pour commencer.</p>
          <router-link to="/dossier/nouveau" class="btn-create">Créer un dossier</router-link>
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

/* Header (form.html style) */
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
  color: #1e293b;
}

.page-title p {
  font-size: 12px;
  color: #64748b;
  margin-top: 1px;
}

.btn-create {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  text-decoration: none;
  font-family: 'Inter', sans-serif;
  transition: opacity 0.15s;
}

.btn-create:hover { opacity: 0.88; }

/* Canvas */
.canvas {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 28px;
}

.stat-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 14px;
  transition: box-shadow 0.15s;
}

.stat-card:hover { box-shadow: 0 2px 8px rgba(0,0,0,0.06); }

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon.total    { background: #eff6ff; color: #2563eb; }
.stat-icon.created  { background: #f5f3ff; color: #7c3aed; }
.stat-icon.validated { background: #f0fdf4; color: #16a34a; }
.stat-icon.cancelled { background: #fef2f2; color: #dc2626; }

.stat-body {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.stat-label {
  font-size: 12px;
  color: #64748b;
  font-weight: 500;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1e293b;
  line-height: 1;
}

.state-msg {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 20px;
}

.error-msg-box {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 20px;
}

.empty-paper {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 48px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 14px;
}

.empty-paper p {
  color: #64748b;
  font-size: 14px;
}
</style>
