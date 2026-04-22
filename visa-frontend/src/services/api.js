const BASE = '/api'

function authHeaders() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

async function handleResponse(res) {
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data.error || `Erreur ${res.status}`)
  return data
}

export async function createDossier(payload) {
  const res = await fetch(`${BASE}/dossiers`, {
    method: 'POST',
    headers: authHeaders(),
    body: JSON.stringify(payload)
  })
  return handleResponse(res)
}

export async function getStatuts() {
  const res = await fetch(`${BASE}/statuts`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getDashboardStats() {
  const res = await fetch(`${BASE}/dossiers/stats`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getDossiers() {
  const res = await fetch(`${BASE}/dossiers`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getDossier(dossierId) {
  const res = await fetch(`${BASE}/dossiers/${dossierId}`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getPiecesCommunes() {
  const res = await fetch(`${BASE}/nomenclatures/pieces/communes`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getPiecesByTypeVisa(typeVisaId) {
  const res = await fetch(`${BASE}/nomenclatures/pieces/${typeVisaId}`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getSituationsFamiliales() {
  const res = await fetch(`${BASE}/nomenclatures/situation-familiale`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function validateDossier(dossierId) {
  const res = await fetch(`${BASE}/dossiers/${dossierId}/valider`, {
    method: 'PUT',
    headers: authHeaders()
  })
  return handleResponse(res)
}

export async function getIndividusComplets() {
  const res = await fetch(`${BASE}/individus/complets`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function getIndividuComplet(individuId) {
  const res = await fetch(`${BASE}/individus/${individuId}/complet`, { headers: authHeaders() })
  return handleResponse(res)
}

export async function searchIndividus(q) {
  const query = (q || '').trim()
  if (!query) return []

  const res = await fetch(`${BASE}/individus/search?q=${encodeURIComponent(query)}`, {
    headers: authHeaders()
  })
  return handleResponse(res)
}
