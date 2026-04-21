<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import SideNav from '../components/SideNav.vue'
import { getDossier, validateDossier } from '../services/api.js'

const route = useRoute()
const router = useRouter()

const dossier = ref(null)
const loading = ref(false)
const errorMsg = ref('')
const validating = ref(false)

const dossierId = computed(() => Number(route.params.id))

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

const canValidate = computed(() => {
  const s = (dossier.value?.statut || '').toUpperCase()
  return s === 'CREE' || s === 'CRÉÉ' || s === 'CREEE'
})

async function load() {
  loading.value = true
  errorMsg.value = ''
  dossier.value = null
  try {
    dossier.value = await getDossier(dossierId.value)
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger le dossier'
  } finally {
    loading.value = false
  }
}

async function handleValidate() {
  if (!canValidate.value || validating.value) return
  validating.value = true
  errorMsg.value = ''
  try {
    await validateDossier(dossierId.value)
    await load()
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de valider le dossier'
  } finally {
    validating.value = false
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
            <h1>Fiche dossier</h1>
            <p v-if="dossier">{{ dossier.numDemande }} · {{ (dossier.statut || '').toUpperCase() }}</p>
            <p v-else>Détail complet</p>
          </div>
        </div>
        <div class="header-right">
          <button v-if="canValidate" class="btn-primary" type="button" :disabled="validating" @click="handleValidate">
            {{ validating ? 'Validation…' : 'Valider' }}
          </button>
        </div>
      </header>

      <main class="canvas">
        <div v-if="errorMsg" class="error-msg-box">{{ errorMsg }}</div>
        <div v-else-if="loading" class="state-msg">Chargement…</div>

        <div v-else-if="dossier" class="content">
          <section class="card">
            <h2>Infos demande</h2>
            <div class="grid">
              <div class="field"><span class="label">Référence</span><span class="value mono">{{ dossier.numDemande }}</span></div>
              <div class="field"><span class="label">Type visa</span><span class="value">{{ dossier.typeVisa }}</span></div>
              <div class="field"><span class="label">Type demande</span><span class="value">{{ dossier.typeDemande }}</span></div>
              <div class="field"><span class="label">Statut</span><span class="value">{{ (dossier.statut || '').toUpperCase() }}</span></div>
              <div class="field"><span class="label">Créé le</span><span class="value">{{ formatDateTime(dossier.dateCreation) }}</span></div>
            </div>
          </section>

          <section class="card">
            <h2>État civil</h2>
            <div class="grid">
              <div class="field"><span class="label">Nom</span><span class="value">{{ dossier.individuNom }}</span></div>
              <div class="field"><span class="label">Prénoms</span><span class="value">{{ dossier.individuPrenoms }}</span></div>
              <div class="field"><span class="label">Date de naissance</span><span class="value">{{ formatDate(dossier.individuDateNaissance) }}</span></div>
              <div class="field" v-if="dossier.individuId">
                <span class="label">Fiche individu</span>
                <router-link class="link" :to="`/individus/${dossier.individuId}`">Voir l’individu</router-link>
              </div>
            </div>
          </section>

          <section class="card">
            <h2>Passeport</h2>
            <div class="grid">
              <div class="field"><span class="label">Numéro</span><span class="value mono">{{ dossier.passeportNumero }}</span></div>
              <div class="field"><span class="label">Délivré le</span><span class="value">{{ formatDate(dossier.passeportDateDelivrance) }}</span></div>
              <div class="field">
                <span class="label">Expire le</span>
                <span class="value" :class="{ expired: isExpiredDate(dossier.passeportDateExpiration) }">
                  {{ formatDate(dossier.passeportDateExpiration) }}
                </span>
              </div>
            </div>
          </section>

          <section class="card">
            <h2>Visa transformable</h2>
            <div class="grid">
              <div class="field"><span class="label">Référence</span><span class="value mono">{{ dossier.visaTransformableReference || '' }}</span></div>
              <div class="field"><span class="label">Date d’entrée</span><span class="value">{{ formatDate(dossier.visaTransformableDateEntree) }}</span></div>
              <div class="field"><span class="label">Lieu d’entrée</span><span class="value">{{ dossier.visaTransformableLieuEntree || '' }}</span></div>
              <div class="field"><span class="label">Fin visa</span><span class="value">{{ formatDate(dossier.visaTransformableDateFinVisa) }}</span></div>
            </div>
          </section>

          <section class="card">
            <h2>Pièces fournies</h2>
            <table class="table">
              <thead>
                <tr><th>Pièce</th><th class="center">Présente</th></tr>
              </thead>
              <tbody>
                <tr v-for="p in (dossier.pieces || [])" :key="p.id">
                  <td>{{ p.libellePiece }}</td>
                  <td class="center">
                    <span class="check" :class="{ ok: p.isPresent }">{{ p.isPresent ? 'Oui' : 'Non' }}</span>
                  </td>
                </tr>
                <tr v-if="(dossier.pieces || []).length === 0">
                  <td colspan="2" class="empty">Aucune pièce.</td>
                </tr>
              </tbody>
            </table>
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

.btn-primary {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: var(--primary-blue);
  color: white;
  padding: 8px 16px;
  border-radius: 6px;
  border: none;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  transition: opacity 0.15s;
}

.btn-primary:disabled { opacity: 0.7; cursor: not-allowed; }

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

.link {
  color: var(--primary-blue);
  text-decoration: none;
  font-weight: 700;
  font-size: 13px;
}

.link:hover { text-decoration: underline; }

.expired { color: var(--danger-red); font-weight: 700; }

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  text-align: left;
  font-size: 12px;
  color: var(--text-gray);
  font-weight: 700;
  padding: 10px 0;
  border-bottom: 1px solid #eef2f7;
}

.table td {
  padding: 10px 0;
  border-bottom: 1px solid #eef2f7;
  font-size: 13px;
  color: var(--text-dark);
}

.center { text-align: center; }

.check {
  display: inline-flex;
  padding: 2px 8px;
  border-radius: 999px;
  border: 1px solid var(--border-color);
  font-weight: 700;
  font-size: 12px;
  color: var(--text-gray);
}

.check.ok {
  color: var(--primary-blue);
  border-color: rgba(21, 112, 176, 0.35);
}

.empty {
  text-align: center;
  color: var(--text-gray);
  padding: 10px 0;
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
