<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createDossier, getPiecesCommunes, getPiecesByTypeVisa } from '../services/api.js'
import SideNav from '../components/SideNav.vue'

const router = useRouter()

const typeDemandeList = JSON.parse(localStorage.getItem('typeDemande') || '[]')
const typeVisaList = JSON.parse(localStorage.getItem('typeVisa') || '[]')

const form = ref({
  typeDemandeId: typeDemandeList[0]?.id ?? null,
  nom: '',
  prenoms: '',
  nomJeuneFille: '',
  dateNaissance: '',
  situationFamiliale: '',
  nationalite: '',
  profession: '',
  adresseMada: '',
  contactMada: '',
  numeroPasseport: '',
  dateDelivrancePasseport: '',
  dateExpirationPasseport: '',
  refVisaTransformable: '',
  dateEntree: '',
  lieuEntree: '',
  dateFinVisa: '',
  typeVisaId: typeVisaList[0]?.id ?? null,
})

const loading = ref(false)
const errorMsg = ref('')
const successDossier = ref(null)
const activeTab = ref('fields')

const piecesCommunes = ref([])
const piecesSpecifiques = ref([])
const checklistCommune = ref([])
const checklistSpecifique = ref([])

const passportExpired = computed(() => {
  if (!form.value.dateExpirationPasseport) return false
  return new Date(form.value.dateExpirationPasseport) < new Date()
})

const selectedTypeVisaLabel = computed(() => {
  const found = typeVisaList.find(t => t.id === form.value.typeVisaId)
  return found?.libelle?.toUpperCase() || ''
})

async function loadPiecesCommunes() {
  try {
    piecesCommunes.value = await getPiecesCommunes()
    checklistCommune.value = piecesCommunes.value.map(() => false)
  } catch { /* vide */ }
}

async function loadPiecesSpecifiques(typeVisaId) {
  if (!typeVisaId) { piecesSpecifiques.value = []; checklistSpecifique.value = []; return }
  try {
    piecesSpecifiques.value = await getPiecesByTypeVisa(typeVisaId)
    checklistSpecifique.value = piecesSpecifiques.value.map(() => false)
  } catch { piecesSpecifiques.value = []; checklistSpecifique.value = [] }
}

watch(() => form.value.typeVisaId, (newId) => loadPiecesSpecifiques(newId))

onMounted(async () => {
  await loadPiecesCommunes()
  await loadPiecesSpecifiques(form.value.typeVisaId)
})

async function handleSubmit() {
  errorMsg.value = ''
  loading.value = true
  try {
    const payload = {
      ...form.value,
      dateNaissance: form.value.dateNaissance || null,
      dateDelivrancePasseport: form.value.dateDelivrancePasseport || null,
      dateExpirationPasseport: form.value.dateExpirationPasseport || null,
      dateEntree: form.value.dateEntree || null,
      dateFinVisa: form.value.dateFinVisa || null,
    }
    successDossier.value = await createDossier(payload)
  } catch (err) {
    errorMsg.value = err.message
  } finally {
    loading.value = false
  }
}

function newDossier() {
  successDossier.value = null
  form.value = {
    typeDemandeId: typeDemandeList[0]?.id ?? null,
    nom: '', prenoms: '', nomJeuneFille: '', dateNaissance: '',
    situationFamiliale: '', nationalite: '', profession: '',
    adresseMada: '', contactMada: '',
    numeroPasseport: '', dateDelivrancePasseport: '', dateExpirationPasseport: '',
    refVisaTransformable: '', dateEntree: '', lieuEntree: '', dateFinVisa: '',
    typeVisaId: typeVisaList[0]?.id ?? null,
  }
  errorMsg.value = ''
  checklistCommune.value = piecesCommunes.value.map(() => false)
  loadPiecesSpecifiques(form.value.typeVisaId)
}
</script>

<template>
  <div class="app-shell">
    <SideNav />

    <div class="page-wrapper">

      <!-- Header bar (form.html exact) -->
      <header class="page-header">
        <div class="header-left">
          <router-link to="/dashboard" class="close-btn" title="Retour">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <line x1="18" y1="6" x2="6" y2="18"/><line x1="6" y1="6" x2="18" y2="18"/>
            </svg>
          </router-link>
          <div class="form-title">
            <h1>Nouveau Dossier</h1>
            <p>Dossier Transformation Visa / Carte de Résident</p>
          </div>
        </div>

        <div class="nav-tabs">
          <div class="nav-tab" :class="{ active: activeTab === 'fields' }" @click="activeTab = 'fields'">Formulaire</div>
          <div class="nav-tab" :class="{ active: activeTab === 'checklist' }" @click="activeTab = 'checklist'">Checklist</div>
        </div>

        <div class="header-right">
          <button type="submit" form="dossierForm" class="btn-save" :disabled="loading">
            {{ loading ? 'Enregistrement…' : 'Enregistrer' }}
          </button>
        </div>
      </header>

      <!-- Succès -->
      <div v-if="successDossier" class="canvas">
        <div class="success-paper">
          <div class="success-icon">
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#16a34a" stroke-width="2.5">
              <polyline points="20,6 9,17 4,12"/>
            </svg>
          </div>
          <h2>Dossier créé avec succès</h2>
          <div class="dossier-ref">{{ successDossier.numDemande }}</div>
          <p>Statut : <strong>CRÉÉ</strong> · ID : {{ successDossier.dossierId }}</p>
          <div class="success-actions">
            <button class="btn-save" @click="newDossier">Nouveau dossier</button>
            <router-link to="/dashboard" class="btn-outline">Tableau de bord</router-link>
          </div>
        </div>
      </div>

      <!-- Formulaire principal -->
      <main v-else class="canvas">

        <p v-if="errorMsg" class="error-banner">{{ errorMsg }}</p>

        <!-- Onglet Formulaire -->
        <form v-show="activeTab === 'fields'" id="dossierForm" @submit.prevent="handleSubmit" class="form-paper">

          <!-- Nature de la procédure -->
          <div class="paper-section">
            <h2>Nature de la procédure</h2>
            <p class="section-desc">Sélectionnez le type de demande</p>
            <div class="form-grid" style="margin-top: 16px;">
              <div class="full-width">
                <label class="field-label">Procédure <span>*</span></label>
                <select class="field-input" v-model="form.typeDemandeId" required>
                  <option v-for="t in typeDemandeList" :key="t.id" :value="t.id">{{ t.libelle }}</option>
                </select>
              </div>
            </div>
          </div>

          <div class="paper-divider"></div>

          <!-- Section 1 : État Civil -->
          <div class="paper-section">
            <h2>1. État Civil de l'Étranger</h2>
            <p class="section-desc">Informations personnelles du demandeur</p>

            <div class="form-grid">
              <div>
                <label class="field-label">Nom <span>*</span></label>
                <input type="text" class="field-input" v-model="form.nom" placeholder="NOM" required />
              </div>
              <div>
                <label class="field-label">Prénoms</label>
                <input type="text" class="field-input" v-model="form.prenoms" placeholder="Prénoms" />
              </div>
              <div>
                <label class="field-label">Nom de jeune fille</label>
                <input type="text" class="field-input" v-model="form.nomJeuneFille" placeholder="Si applicable" />
              </div>
              <div>
                <label class="field-label">Date de naissance <span>*</span></label>
                <input type="date" class="field-input" v-model="form.dateNaissance" required />
              </div>
              <div>
                <label class="field-label">Situation familiale</label>
                <select class="field-input" v-model="form.situationFamiliale">
                  <option value="">— Sélectionner —</option>
                  <option value="CELIBATAIRE">Célibataire</option>
                  <option value="MARIE">Marié(e)</option>
                  <option value="DIVORCE">Divorcé(e)</option>
                  <option value="VEUF">Veuf / Veuve</option>
                </select>
              </div>
              <div>
                <label class="field-label">Nationalité actuelle <span>*</span></label>
                <input type="text" class="field-input" v-model="form.nationalite" placeholder="Ex : Française" required />
              </div>
              <div>
                <label class="field-label">Profession</label>
                <input type="text" class="field-input" v-model="form.profession" placeholder="Métier exercé" />
              </div>
              <div>
                <label class="field-label">Contact</label>
                <input type="text" class="field-input" v-model="form.contactMada" placeholder="Téléphone ou email" />
              </div>
              <div class="full-width">
                <label class="field-label">Adresse à Madagascar</label>
                <input type="text" class="field-input" v-model="form.adresseMada" placeholder="Adresse complète" />
              </div>
            </div>
          </div>

          <div class="paper-divider"></div>

          <!-- Section 2 : Voyage & Passeport -->
          <div class="paper-section">
            <h2>2. Détails du Voyage &amp; Passeport</h2>
            <p class="section-desc">Informations du passeport et du voyage</p>

            <div class="form-grid">
              <div>
                <label class="field-label">Numéro de Passeport <span>*</span></label>
                <input type="text" class="field-input" v-model="form.numeroPasseport" placeholder="Ex : AB123456" required />
              </div>
              <div>
                <label class="field-label">Date de délivrance</label>
                <input type="date" class="field-input" v-model="form.dateDelivrancePasseport" />
              </div>
              <div>
                <label class="field-label">Date d'expiration</label>
                <input
                  type="date"
                  class="field-input"
                  v-model="form.dateExpirationPasseport"
                  :class="{ 'field-warn-input': passportExpired }"
                />
                <span v-if="passportExpired" class="field-warn-text">Attention : passeport expiré</span>
              </div>
              <div>
                <label class="field-label">Réf. Visa Transformable <span>*</span></label>
                <input type="text" class="field-input" v-model="form.refVisaTransformable" placeholder="Numéro du visa actuel" required />
              </div>
              <div>
                <label class="field-label">Date d'entrée à Madagascar</label>
                <input type="date" class="field-input" v-model="form.dateEntree" />
              </div>
              <div>
                <label class="field-label">Lieu d'entrée (Aéroport/Port)</label>
                <input type="text" class="field-input" v-model="form.lieuEntree" placeholder="Ex : Ivato, Nosy Be" />
              </div>
              <div>
                <label class="field-label">Date de fin du visa actuel</label>
                <input type="date" class="field-input" v-model="form.dateFinVisa" />
              </div>
            </div>
          </div>

          <div class="paper-divider"></div>

          <!-- Submit dans le header, mais aussi en bas -->
          <div class="form-footer">
            <span v-if="passportExpired" class="warn-badge">Passeport expiré — enregistrement toujours possible</span>
            <button type="submit" class="btn-save" :disabled="loading">
              {{ loading ? 'Enregistrement…' : 'Enregistrer &amp; Générer le Numéro de Dossier' }}
            </button>
          </div>
        </form>

        <!-- Onglet Checklist -->
        <div v-show="activeTab === 'checklist'" class="form-paper">
          <div class="paper-section">
            <h2>3. Dossiers à fournir</h2>
            <p class="section-desc">Cochez les pièces remises par le demandeur</p>

            <div class="form-grid" style="margin-top: 16px; margin-bottom: 24px;">
              <div>
                <label class="field-label">Catégorie de l'immigrant <span>*</span></label>
                <select class="field-input" v-model="form.typeVisaId">
                  <option v-for="t in typeVisaList" :key="t.id" :value="t.id">{{ t.libelle }}</option>
                </select>
              </div>
            </div>

            <div class="checklist-grid">
              <div class="checklist-card">
                <p class="checklist-heading">PIÈCES COMMUNES</p>
                <label v-for="(piece, i) in piecesCommunes" :key="i" class="check-row">
                  <input type="checkbox" v-model="checklistCommune[i]" />
                  <span>{{ piece }}</span>
                </label>
                <p v-if="piecesCommunes.length === 0" class="check-empty">Chargement…</p>
              </div>

              <div class="checklist-card">
                <p class="checklist-heading">PIÈCES COMPLÉMENTAIRES — {{ selectedTypeVisaLabel }}</p>
                <label v-for="(piece, i) in piecesSpecifiques" :key="i" class="check-row">
                  <input type="checkbox" v-model="checklistSpecifique[i]" />
                  <span>{{ piece }}</span>
                </label>
                <p v-if="piecesSpecifiques.length === 0" class="check-empty">Aucune pièce spécifique.</p>
              </div>
            </div>
          </div>
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

/* Header (form.html exact) */
.page-header {
  background: white;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e2e8f0;
  flex-shrink: 0;
  gap: 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.close-btn {
  color: #64748b;
  cursor: pointer;
  display: flex;
  align-items: center;
  text-decoration: none;
  transition: color 0.15s;
}

.close-btn:hover { color: #1e293b; }

.form-title h1 {
  font-size: 15px;
  font-weight: 600;
  color: #1e293b;
}

.form-title p {
  font-size: 12px;
  color: #64748b;
}

/* Nav tabs (form.html exact) */
.nav-tabs {
  display: flex;
  background: #f8fafc;
  padding: 4px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
}

.nav-tab {
  padding: 6px 18px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border-radius: 6px;
  color: #64748b;
  transition: all 0.15s;
}

.nav-tab.active {
  background: white;
  color: #1e293b;
  box-shadow: 0 1px 3px rgba(0,0,0,0.1);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.btn-save {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: #2563eb;
  color: white;
  padding: 8px 18px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  transition: opacity 0.15s;
}

.btn-save:hover:not(:disabled) { opacity: 0.88; }
.btn-save:disabled { background: #cbd5e1; cursor: not-allowed; }

/* Canvas */
.canvas {
  flex: 1;
  padding: 32px 40px;
  overflow-y: auto;
}

.error-banner {
  background: #fef2f2;
  border: 1px solid #fecaca;
  color: #b91c1c;
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 20px;
}

/* Form paper (form.html exact) */
.form-paper {
  background: white;
  max-width: 860px;
  margin: 0 auto;
  min-height: 100%;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.paper-section {
  padding: 32px 40px;
}

.paper-section h2 {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin-bottom: 4px;
}

.section-desc {
  font-size: 13px;
  color: #64748b;
  margin-bottom: 0;
}

.paper-divider {
  height: 1px;
  background: #f1f5f9;
  margin: 0 40px;
}

/* Form grid (form.html exact) */
.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}

.full-width { grid-column: span 2; }

.field-label {
  display: block;
  font-size: 13px;
  font-weight: 500;
  color: #1e293b;
  margin-bottom: 6px;
}

.field-label span { color: #ef4444; }

.field-input {
  width: 100%;
  padding: 11px 12px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  background: #fdfdfd;
  font-size: 14px;
  color: #1e293b;
  font-family: 'Inter', sans-serif;
  outline: none;
  transition: border-color 0.15s, box-shadow 0.15s;
}

.field-input:focus {
  border-color: #2563eb;
  box-shadow: 0 0 0 3px rgba(37,99,235,0.1);
  background: white;
}

.field-warn-input { border-color: #f59e0b !important; }
.field-warn-text { font-size: 12px; color: #d97706; font-weight: 500; margin-top: 4px; display: block; }

/* Form footer */
.form-footer {
  padding: 24px 40px 32px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
  border-top: 1px solid #f1f5f9;
}

.warn-badge {
  font-size: 12px;
  color: #d97706;
  background: #fffbeb;
  border: 1px solid #fde68a;
  padding: 6px 12px;
  border-radius: 6px;
}

/* Checklist */
.checklist-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 8px;
}

.checklist-card {
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 20px;
  background: #fdfdfd;
}

.checklist-heading {
  font-size: 11px;
  font-weight: 700;
  color: #1e293b;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 14px;
}

.check-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 13px;
  color: #1e293b;
  cursor: pointer;
}

.check-row input[type="checkbox"] {
  margin-top: 2px;
  flex-shrink: 0;
  cursor: pointer;
  accent-color: #2563eb;
}

.check-empty {
  color: #94a3b8;
  font-size: 13px;
  font-style: italic;
}

/* Success */
.success-paper {
  background: white;
  max-width: 860px;
  margin: 0 auto;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
  padding: 60px 40px;
  text-align: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.success-icon {
  width: 60px;
  height: 60px;
  background: #f0fdf4;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.success-paper h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
}

.dossier-ref {
  font-size: 26px;
  font-weight: 800;
  color: #2563eb;
  letter-spacing: 2px;
}

.success-paper p { color: #64748b; font-size: 14px; }

.success-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.btn-outline {
  display: inline-flex;
  align-items: center;
  background: white;
  color: #2563eb;
  padding: 8px 18px;
  border: 1px solid #2563eb;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
  cursor: pointer;
  transition: background 0.15s;
}

.btn-outline:hover { background: #eff6ff; }
</style>
