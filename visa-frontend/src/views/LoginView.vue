<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const identifiant = ref('')
const mdp = ref('')
const errorMsg = ref('')
const loading = ref(false)

async function handleLogin() {
  loading.value = true
  errorMsg.value = ''

  try {
    const res = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ identifiant: identifiant.value, mdp: mdp.value })
    })

    const data = await res.json()

    if (!res.ok) throw new Error(data.error || 'Erreur de connexion')

    localStorage.setItem('token', data.token)
    localStorage.setItem('userId', data.userId)
    localStorage.setItem('identifiant', data.identifiant)
    localStorage.setItem('role', data.role)
    localStorage.setItem('permissions', JSON.stringify(data.permissions))
    localStorage.setItem('statuts', JSON.stringify(data.statuts))
    localStorage.setItem('typeVisa', JSON.stringify(data.typeVisa))
    localStorage.setItem('typeDemande', JSON.stringify(data.typeDemande))

    router.push('/dashboard')
  } catch (err) {
    errorMsg.value = err.message
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="main-container">
    <div class="login-section">
      <div class="form-wrapper">
        <div class="logo">
          <h1 class="visa-logo">Visa</h1>
        </div>

        <div class="header-text">
          <h2>Connexion à votre compte</h2>
          <p>Veuillez saisir vos identifiants</p>
        </div>

        <form @submit.prevent="handleLogin">
          <div class="input-group">
            <label for="identifiant">Identifiant</label>
            <input
              id="identifiant"
              v-model="identifiant"
              type="text"
              placeholder="Votre identifiant"
              autocomplete="username"
              required
            />
          </div>

          <div class="input-group">
            <label for="mdp">Mot de passe</label>
            <input
              id="mdp"
              v-model="mdp"
              type="password"
              placeholder="••••••••"
              autocomplete="current-password"
              required
            />
          </div>

          <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

          <button type="submit" class="btn-primary" :disabled="loading">
            {{ loading ? 'Connexion...' : 'Se connecter' }}
          </button>
        </form>
      </div>
    </div>

    <div class="hero-section hero-image-bg"></div>
  </div>
</template>
