const BASE = '/api/public/suivi'

async function handleResponse(res) {
  const data = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(data?.message || data?.error || `Erreur ${res.status}`)
  return data
}

export async function getSuiviByToken(token) {
  const t = String(token || '').trim()
  if (!t) throw new Error('Token obligatoire')
  const res = await fetch(`${BASE}/${encodeURIComponent(t)}`)
  return handleResponse(res)
}

export async function getSuiviByDemandeNumero(demandeNumero) {
  const v = String(demandeNumero || '').trim()
  if (!v) throw new Error('Numéro de demande obligatoire')
  const res = await fetch(`${BASE}?demandeNumero=${encodeURIComponent(v)}`)
  return handleResponse(res)
}

export async function getSuiviByPasseportNumero(passeportNumero) {
  const v = String(passeportNumero || '').trim()
  if (!v) throw new Error('Numéro de passeport obligatoire')
  const res = await fetch(`${BASE}?passeportNumero=${encodeURIComponent(v)}`)
  return handleResponse(res)
}
