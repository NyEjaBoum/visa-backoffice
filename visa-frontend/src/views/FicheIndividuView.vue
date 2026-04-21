<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SideNav from '../components/SideNav.vue'
import { getIndividuComplet } from '../services/api.js'

const route = useRoute()
const router = useRouter()

const individu = ref(null)
const loading = ref(false)
const errorMsg = ref('')

const individuId = computed(() => Number(route.params.id))

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

const passportExpired = computed(() => isExpiredDate(individu.value?.dateExpiration))

async function load() {
  loading.value = true
  errorMsg.value = ''
  individu.value = null
  try {
    individu.value = await getIndividuComplet(individuId.value)
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger la fiche individu'
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
        <div class="header-left">
          <button class="back-btn" type="button" title="Retour" @click="router.back()">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <polyline points="15 18 9 12 15 6"/>
            </svg>
          </button>
          <div class="page-title">
            <h1>Fiche individu</h1>
            <p v-if="individu">{{ individu.nom }} {{ individu.prenom }}</p>
            <p v-else>Détail complet</p>
          </div>
        </div>
      </header>

      <main class="canvas">
        <div v-if="errorMsg" class="error-msg-box">{{ errorMsg }}</div>
        <div v-else-if="loading" class="state-msg">Chargement…</div>

        <div v-else-if="individu" class="content">
          <section class="card">
            <h2>État civil</h2>
            <div class="grid">
              <div class="field"><span class="label">Nom</span><span class="value">{{ individu.nom }}</span></div>
              <div class="field"><span class="label">Prénom</span><span class="value">{{ individu.prenom }}</span></div>
              <div class="field"><span class="label">Nom de jeune fille</span><span class="value">{{ individu.nomJeuneFille || '' }}</span></div>
              <div class="field"><span class="label">Date de naissance</span><span class="value">{{ formatDate(individu.dateNaissance) }}</span></div>
              <div class="field"><span class="label">Situation familiale</span><span class="value">{{ individu.situationFamiliale || '' }}</span></div>
              <div class="field"><span class="label">Nationalité</span><span class="value">{{ individu.nationalite || '' }}</span></div>
              <div class="field"><span class="label">Profession</span><span class="value">{{ individu.profession || '' }}</span></div>
            </div>
          </section>

          <section class="card">
            <h2>Coordonnées</h2>
            <div class="grid">
              <div class="field"><span class="label">Adresse (Madagascar)</span><span class="value">{{ individu.adresseMada || '' }}</span></div>
              <div class="field"><span class="label">Contact</span><span class="value">{{ individu.contact || '' }}</span></div>
            </div>
          </section>

          <section class="card">
            <h2>Passeport</h2>
            <div class="grid">
              <div class="field"><span class="label">Numéro</span><span class="value mono">{{ individu.passeportNumero || '' }}</span></div>
              <div class="field"><span class="label">Délivré le</span><span class="value">{{ formatDate(individu.dateDelivrance) }}</span></div>
              <div class="field">
                <span class="label">Expire le</span>
                <span class="value" :class="{ expired: passportExpired }">
                  {{ formatDate(individu.dateExpiration) }}
                </span>
              </div>
              <div class="field" v-if="passportExpired">
                <span class="label">Statut</span>
                <span class="tag tag--danger">Expiré</span>
              </div>
            </div>
          </section>
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

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.back-btn {
  width: 34px;
  height: 34px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  color: #475569;
  cursor: pointer;
}

.back-btn:hover { background: #f8fafc; }

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

.content {
  display: grid;
  gap: 14px;
}

.card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px;
}

.card h2 {
  font-size: 13px;
  font-weight: 700;
  color: var(--text-dark);
  margin-bottom: 10px;
}

.grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px 14px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.label {
  font-size: 12px;
  color: var(--text-gray);
  font-weight: 600;
}

.value {
  font-size: 13px;
  color: var(--text-dark);
}

.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace; }

.expired { color: var(--danger-red); font-weight: 700; }

.tag {
  display: inline-flex;
  align-items: center;
  padding: 2px 10px;
  border-radius: 999px;
  border: 1px solid var(--border-color);
  font-size: 12px;
  font-weight: 800;
}

.tag--danger {
  color: var(--danger-red);
  border-color: rgba(211, 47, 47, 0.35);
}

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
</style>
