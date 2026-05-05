<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getSuiviByToken,
  getSuiviByDemandeNumero,
  getSuiviByPasseportNumero
} from '../services/api.js'

const route = useRoute()
const router = useRouter()

const tokenInput = ref('')
const demandeNumeroInput = ref('')
const passeportNumeroInput = ref('')

const loading = ref(false)
const errorMsg = ref('')

// Vue détail unique (recherche par QR token)
const suivi = ref(null)
// Vue liste (recherche par numéro de demande ou passeport)
const suiviList = ref(null)
// Numéro de la demande dont l'historique est affiché (accordion)
const expandedNumero = ref(null)

function clearResults() {
  suivi.value = null
  suiviList.value = null
  errorMsg.value = ''
}

async function loadByToken(token) {
  loading.value = true
  clearResults()
  try {
    suivi.value = await getSuiviByToken(token)
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger le suivi'
  } finally {
    loading.value = false
  }
}

async function loadByDemandeNumero(demandeNumero) {
  loading.value = true
  clearResults()
  try {
    suiviList.value = await getSuiviByDemandeNumero(demandeNumero)
    expandedNumero.value = suiviList.value?.demandeSelectionneeNumero || null
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger le suivi'
  } finally {
    loading.value = false
  }
}

async function loadByPasseportNumero(passeportNumero) {
  loading.value = true
  clearResults()
  try {
    suiviList.value = await getSuiviByPasseportNumero(passeportNumero)
    expandedNumero.value = suiviList.value?.demandeSelectionneeNumero || null
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger le suivi'
  } finally {
    loading.value = false
  }
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

function badgeClass(statut) {
  const libelle = (statut || '').toUpperCase()
  if (!libelle) return 'badge badge--muted'
  if (libelle.includes('REFUS') || libelle.includes('REJETE')) return 'badge badge--danger'
  if (libelle.includes('APPROUV') || libelle.includes('VALID')) return 'badge badge--success'
  if (libelle.includes('SCAN')) return 'badge badge--primary'
  return 'badge badge--muted'
}

function toggleExpand(numDemande) {
  expandedNumero.value = expandedNumero.value === numDemande ? null : numDemande
}

async function loadFromRoute() {
  const token = route.params.token
  const demandeNumero = route.query.demandeNumero
  const passeportNumero = route.query.passeportNumero

  if (token) {
    tokenInput.value = String(token)
    demandeNumeroInput.value = ''
    passeportNumeroInput.value = ''
    await loadByToken(token)
    return
  }

  if (demandeNumero) {
    tokenInput.value = ''
    demandeNumeroInput.value = String(demandeNumero)
    passeportNumeroInput.value = ''
    await loadByDemandeNumero(demandeNumero)
    return
  }

  if (passeportNumero) {
    tokenInput.value = ''
    demandeNumeroInput.value = ''
    passeportNumeroInput.value = String(passeportNumero)
    await loadByPasseportNumero(passeportNumero)
  }
}

async function submit() {
  clearResults()

  const t = tokenInput.value.trim()
  const dn = demandeNumeroInput.value.trim()
  const pn = passeportNumeroInput.value.trim()

  try {
    if (t) {
      const currentToken = route.params.token
      if (currentToken && String(currentToken) === t) {
        await loadByToken(t)
        return
      }
      await router.push(`/suivi/${encodeURIComponent(t)}`)
      return
    }

    if (dn) {
      const currentDn = route.query.demandeNumero
      if (route.path === '/suivi' && currentDn && String(currentDn) === dn) {
        await loadByDemandeNumero(dn)
        return
      }
      await router.push({ path: '/suivi', query: { demandeNumero: dn } })
      return
    }

    if (pn) {
      const currentPn = route.query.passeportNumero
      if (route.path === '/suivi' && currentPn && String(currentPn) === pn) {
        await loadByPasseportNumero(pn)
        return
      }
      await router.push({ path: '/suivi', query: { passeportNumero: pn } })
      return
    }

    throw new Error('Saisis un token QR, un numéro de demande, ou un numéro de passeport')
  } catch (e) {
    errorMsg.value = e?.message || 'Erreur'
  }
}

onMounted(() => {
  loadFromRoute()
})

watch(
  () => [route.params.token, route.query.demandeNumero, route.query.passeportNumero],
  () => {
    loadFromRoute()
  }
)
</script>

<template>
  <div class="page-wrapper">
    <header class="page-header">
      <div class="page-title">
        <h1>Suivi de demande</h1>
        <p>Scanne le QR Code ou saisis une référence</p>
      </div>
    </header>

    <main class="canvas">
      <!-- Formulaire de recherche -->
      <div class="paper">
        <form class="form" @submit.prevent="submit">
          <div class="grid">
            <div class="field">
              <label>Token QR</label>
              <input v-model="tokenInput" placeholder="UUID du QR Code" />
              <small>Si tu as un lien QR, colle juste le token.</small>
            </div>

            <div class="field">
              <label>Numéro de demande</label>
              <input v-model="demandeNumeroInput" placeholder="Ex: MADA-2026-0001" />
            </div>

            <div class="field">
              <label>Numéro de passeport</label>
              <input v-model="passeportNumeroInput" placeholder="Ex: AB123456" />
            </div>

            <div class="field actions">
              <button class="btn-primary" type="submit" :disabled="loading">
                {{ loading ? 'Chargement…' : 'Consulter' }}
              </button>
            </div>
          </div>
        </form>
      </div>

      <div v-if="errorMsg" class="error-msg-box">{{ errorMsg }}</div>
      <div v-else-if="loading" class="state-msg">Chargement…</div>

      <!-- Vue détail unique (QR token) -->
      <div v-else-if="suivi" class="paper result-paper">
        <div class="summary">
          <div>
            <div class="k">Demande</div>
            <div class="v mono">{{ suivi.demandeNumero }}</div>
          </div>
          <div>
            <div class="k">Demandeur</div>
            <div class="v">{{ (suivi.demandeurNom || '') + (suivi.demandeurPrenoms ? ' ' + suivi.demandeurPrenoms : '') }}</div>
          </div>
          <div>
            <div class="k">Statut actuel</div>
            <div class="v"><span :class="badgeClass(suivi.statutActuel)">{{ (suivi.statutActuel || '').toUpperCase() }}</span></div>
          </div>
          <div>
            <div class="k">Créé le</div>
            <div class="v">{{ formatDateTime(suivi.demandeDateCreation) }}</div>
          </div>
        </div>

        <table class="table">
          <thead>
            <tr>
              <th>Étape</th>
              <th>Date / Heure</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(h, idx) in (suivi.historique || [])" :key="idx">
              <td><span :class="badgeClass(h.statut)">{{ (h.statut || '').toUpperCase() }}</span></td>
              <td>{{ formatDateTime(h.dateHeure) }}</td>
            </tr>
            <tr v-if="(suivi.historique || []).length === 0">
              <td colspan="2" class="empty">Aucun historique.</td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Vue liste (numéro de demande ou passeport) -->
      <div v-else-if="suiviList" class="result-paper">
        <div class="demandeur-header paper">
          <div class="k">Demandeur</div>
          <div class="demandeur-name">{{ suiviList.demandeurNom }} {{ suiviList.demandeurPrenoms }}</div>
          <div class="demande-count">{{ suiviList.demandes.length }} demande{{ suiviList.demandes.length > 1 ? 's' : '' }}</div>
        </div>

        <div
          v-for="item in suiviList.demandes"
          :key="item.demandeNumero"
          class="demande-card"
          :class="{ 'demande-card--selected': item.demandeNumero === suiviList.demandeSelectionneeNumero }"
        >
          <div class="demande-card-header" @click="toggleExpand(item.demandeNumero)">
            <div class="demande-card-info">
              <div class="demande-card-num mono">{{ item.demandeNumero }}</div>
              <div class="demande-card-date">{{ formatDateTime(item.demandeDateCreation) }}</div>
            </div>
            <div class="demande-card-right">
              <span :class="badgeClass(item.statutActuel)">{{ (item.statutActuel || '—').toUpperCase() }}</span>
              <span class="chevron" :class="{ 'chevron--open': expandedNumero === item.demandeNumero }">›</span>
            </div>
          </div>

          <div v-if="expandedNumero === item.demandeNumero" class="demande-card-body">
            <table class="table">
              <thead>
                <tr>
                  <th>Étape</th>
                  <th>Date / Heure</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(h, idx) in (item.historique || [])" :key="idx">
                  <td><span :class="badgeClass(h.statut)">{{ (h.statut || '').toUpperCase() }}</span></td>
                  <td>{{ formatDateTime(h.dateHeure) }}</td>
                </tr>
                <tr v-if="(item.historique || []).length === 0">
                  <td colspan="2" class="empty">Aucun historique.</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.page-wrapper {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.page-header {
  background: white;
  padding: 12px 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e2e8f0;
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
}

.paper {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.result-paper {
  margin-top: 14px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.form {
  padding: 14px;
}

.grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.field label {
  font-size: 12px;
  color: var(--text-gray);
  font-weight: 600;
}

.field input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  font-family: 'Inter', sans-serif;
}

.field input:focus {
  border-color: var(--primary-blue);
}

.field small {
  color: var(--text-gray);
  font-size: 12px;
}

.actions {
  align-items: flex-end;
  justify-content: flex-end;
}

.btn-primary {
  width: 100%;
  background-color: var(--primary-blue);
  color: white;
  padding: 11px 12px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  transition: opacity 0.2s;
}

.btn-primary:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* ── Vue détail unique ──────────────────────────────── */

.summary {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  padding: 14px;
  border-bottom: 1px solid #eef2f7;
}

.k {
  font-size: 12px;
  color: var(--text-gray);
  font-weight: 600;
}

.v {
  font-size: 13px;
  color: var(--text-dark);
  margin-top: 4px;
}

.mono {
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, monospace;
}

/* ── Vue liste (accordion) ──────────────────────────── */

.demandeur-header {
  padding: 14px;
}

.demandeur-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-dark);
  margin-top: 4px;
}

.demande-count {
  font-size: 12px;
  color: var(--text-gray);
  margin-top: 2px;
}

.demande-card {
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.demande-card--selected {
  border-color: var(--primary-blue);
  box-shadow: 0 0 0 2px rgba(21, 112, 176, 0.12);
}

.demande-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 14px;
  cursor: pointer;
  user-select: none;
  transition: background 0.15s;
}

.demande-card-header:hover {
  background: #fafafa;
}

.demande-card-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.demande-card-num {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-dark);
}

.demande-card-date {
  font-size: 12px;
  color: var(--text-gray);
}

.demande-card-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.chevron {
  font-size: 18px;
  color: var(--text-gray);
  display: inline-block;
  transform: rotate(0deg);
  transition: transform 0.2s;
  line-height: 1;
}

.chevron--open {
  transform: rotate(90deg);
}

.demande-card-body {
  border-top: 1px solid #eef2f7;
}

/* ── Table commune ──────────────────────────────────── */

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

/* ── Badges ─────────────────────────────────────────── */

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
.badge--success { color: #15803d; border-color: rgba(21, 128, 61, 0.35); }
.badge--danger { color: var(--danger-red); border-color: rgba(211, 47, 47, 0.35); }

/* ── États ──────────────────────────────────────────── */

.state-msg {
  font-size: 13px;
  color: var(--text-gray);
  margin-top: 12px;
}

.error-msg-box {
  background: rgba(211, 47, 47, 0.06);
  border: 1px solid rgba(211, 47, 47, 0.20);
  color: var(--danger-red);
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-top: 12px;
}

.empty {
  text-align: center;
  color: var(--text-gray);
  padding: 18px 14px;
}

@media (max-width: 920px) {
  .grid {
    grid-template-columns: 1fr;
  }
  .summary {
    grid-template-columns: 1fr;
  }
}
</style>
