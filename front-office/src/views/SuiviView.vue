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
const suivi = ref(null)

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
  if (libelle.includes('APPROUV') || libelle.includes('VALID')) return 'badge badge--primary'
  if (libelle.includes('SCAN')) return 'badge badge--primary'
  return 'badge badge--muted'
}

async function loadByToken(token) {
  loading.value = true
  errorMsg.value = ''
  suivi.value = null
  try {
    suivi.value = await getSuiviByToken(token)
  } catch (e) {
    errorMsg.value = e?.message || 'Impossible de charger le suivi'
  } finally {
    loading.value = false
  }
}

async function submit() {
  loading.value = true
  errorMsg.value = ''
  suivi.value = null

  const t = tokenInput.value.trim()
  const dn = demandeNumeroInput.value.trim()
  const pn = passeportNumeroInput.value.trim()

  try {
    if (t) {
      await router.push(`/suivi/${encodeURIComponent(t)}`)
      return
    }

    if (dn) {
      suivi.value = await getSuiviByDemandeNumero(dn)
      return
    }

    if (pn) {
      suivi.value = await getSuiviByPasseportNumero(pn)
      return
    }

    throw new Error('Saisis un token QR, un numéro de demande, ou un numéro de passeport')
  } catch (e) {
    errorMsg.value = e?.message || 'Erreur'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const token = route.params.token
  if (token) {
    tokenInput.value = String(token)
    loadByToken(token)
  }
})

watch(
  () => route.params.token,
  (token) => {
    if (!token) return
    tokenInput.value = String(token)
    loadByToken(token)
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

      <div v-else-if="suivi" class="paper" style="margin-top: 14px;">
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
              <td>
                <span :class="badgeClass(h.statut)">{{ (h.statut || '').toUpperCase() }}</span>
              </td>
              <td>{{ formatDateTime(h.dateHeure) }}</td>
            </tr>
            <tr v-if="(suivi.historique || []).length === 0">
              <td colspan="2" class="empty">Aucun historique.</td>
            </tr>
          </tbody>
        </table>
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
