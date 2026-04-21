<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const identifiant = localStorage.getItem('identifiant') || ''
const role = localStorage.getItem('role') || ''

function logout() {
  localStorage.clear()
  router.push('/login')
}
</script>

<template>
  <div class="sidebar-container">

    <!-- Rail étroit à gauche -->
    <div class="sidebar-rail">
      <div class="logo-container">
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#f36d21" stroke-width="2.5">
          <circle cx="12" cy="12" r="5"/>
          <line x1="12" y1="1" x2="12" y2="3"/>
          <line x1="12" y1="21" x2="12" y2="23"/>
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/>
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/>
          <line x1="1" y1="12" x2="3" y2="12"/>
          <line x1="21" y1="12" x2="23" y2="12"/>
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/>
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/>
        </svg>
      </div>

      <router-link to="/dashboard" class="rail-icon" :class="{ active: route.path === '/dashboard' }" title="Tableau de bord">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
          <rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>
        </svg>
      </router-link>

      <router-link to="/dossiers" class="rail-icon" :class="{ active: route.path.startsWith('/dossiers') }" title="Dossiers">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14,2 14,8 20,8"/>
          <line x1="8" y1="13" x2="16" y2="13"/>
          <line x1="8" y1="17" x2="16" y2="17"/>
        </svg>
      </router-link>

      <router-link to="/dossier/nouveau" class="rail-icon" :class="{ active: route.path === '/dossier/nouveau' }" title="Nouveau Dossier">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
          <polyline points="14,2 14,8 20,8"/>
          <line x1="12" y1="18" x2="12" y2="12"/>
          <line x1="9" y1="15" x2="15" y2="15"/>
        </svg>
      </router-link>

      <router-link to="/individus" class="rail-icon" :class="{ active: route.path.startsWith('/individus') }" title="Individus">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
          <circle cx="12" cy="7" r="4"/>
        </svg>
      </router-link>

      <div class="rail-bottom">
        <div class="rail-icon" :title="identifiant + ' · ' + role">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
            <circle cx="12" cy="7" r="4"/>
          </svg>
        </div>
        <div class="rail-icon" title="Déconnexion" @click="logout">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16,17 21,12 16,7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
        </div>
      </div>
    </div>

    <!-- Panneau principal -->
    <div class="sidebar-content">
      <h1>Visa</h1>

      <div class="user-block">
        <div class="user-avatar">{{ identifiant.charAt(0).toUpperCase() }}</div>
        <div class="user-info">
          <span class="user-name">{{ identifiant }}</span>
          <span class="user-role">{{ role }}</span>
        </div>
      </div>

      <ul class="nav-list">
        <li>
          <router-link
            to="/dashboard"
            class="nav-item"
            :class="{ active: route.path === '/dashboard' }"
          >
            <div class="nav-item-left">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/>
                <rect x="14" y="14" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/>
              </svg>
              Tableau de bord
            </div>
          </router-link>
        </li>

        <li>
          <router-link
            to="/dossiers"
            class="nav-item"
            :class="{ active: route.path.startsWith('/dossiers') }"
          >
            <div class="nav-item-left">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14,2 14,8 20,8"/>
                <line x1="8" y1="13" x2="16" y2="13"/>
                <line x1="8" y1="17" x2="16" y2="17"/>
              </svg>
              Dossiers
            </div>
          </router-link>
        </li>

        <li>
          <router-link
            to="/dossier/nouveau"
            class="nav-item"
            :class="{ active: route.path === '/dossier/nouveau' }"
          >
            <div class="nav-item-left">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/>
                <polyline points="14,2 14,8 20,8"/>
                <line x1="12" y1="18" x2="12" y2="12"/>
                <line x1="9" y1="15" x2="15" y2="15"/>
              </svg>
              Nouveau Dossier
            </div>
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" stroke-width="2">
              <line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/>
            </svg>
          </router-link>
        </li>

        <li>
          <router-link
            to="/individus"
            class="nav-item"
            :class="{ active: route.path.startsWith('/individus') }"
          >
            <div class="nav-item-left">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/>
                <circle cx="12" cy="7" r="4"/>
              </svg>
              Individus
            </div>
          </router-link>
        </li>
      </ul>

      <div class="sidebar-footer">
        <button class="btn-logout" @click="logout">
          <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/>
            <polyline points="16,17 21,12 16,7"/>
            <line x1="21" y1="12" x2="9" y2="12"/>
          </svg>
          Déconnexion
        </button>
      </div>
    </div>

  </div>
</template>

<style scoped>
.sidebar-container {
  display: flex;
  width: 280px;
  height: 100vh;
  background: #ffffff;
  border-right: 1px solid #e5e5e5;
  flex-shrink: 0;
  position: sticky;
  top: 0;
}

/* Rail */
.sidebar-rail {
  width: 56px;
  background: #f5f5f7;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  border-right: 1px solid #e5e5e5;
  gap: 4px;
}

.logo-container {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rail-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #737373;
  border-radius: 8px;
  transition: background 0.15s, color 0.15s;
  cursor: pointer;
  text-decoration: none;
}

.rail-icon:hover { background: #e8e8e9; color: #1a1a1a; }
.rail-icon.active { background: #e8e8e9; color: #1a1a1a; }

.rail-bottom {
  margin-top: auto;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* Content panel */
.sidebar-content {
  flex: 1;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.sidebar-content h1 {
  font-size: 17px;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16px;
  font-style: italic;
  letter-spacing: -0.3px;
}

.user-block {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px;
  background: #f5f5f7;
  border-radius: 8px;
  margin-bottom: 20px;
}

.user-avatar {
  width: 30px;
  height: 30px;
  background: #e8e8e9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 700;
  color: #1a1a1a;
  flex-shrink: 0;
}

.user-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.user-name {
  font-size: 13px;
  font-weight: 600;
  color: #1a1a1a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-role {
  font-size: 11px;
  color: #737373;
  text-transform: uppercase;
  letter-spacing: 0.04em;
}

/* Nav list */
.nav-list {
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: 2px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 8px;
  border-radius: 6px;
  cursor: pointer;
  color: #1a1a1a;
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  transition: background 0.15s;
}

.nav-item:hover { background: #f4f4f5; }
.nav-item.active { background: #f4f4f5; }

.nav-item-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

/* Footer */
.sidebar-footer {
  padding-top: 16px;
  border-top: 1px solid #e5e5e5;
}

.btn-logout {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  padding: 8px 8px;
  background: transparent;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  color: #737373;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  transition: background 0.15s, color 0.15s;
}

.btn-logout:hover { background: #f4f4f5; color: #1a1a1a; }
</style>
