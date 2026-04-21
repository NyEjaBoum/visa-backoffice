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

export async function getDashboardStats() {
  const res = await fetch(`${BASE}/dossiers/stats`, { headers: authHeaders() })
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
