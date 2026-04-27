# SPRINT 4 : Suivi via API (Vue.js)

## 🎯 User Story
**En tant qu'usager**, je veux voir l'historique complet des statuts de ma demande via mon QR Code.

> ⚠️ Remarque : Un QR Code est généré **par demande**.

---

## 🧩 Scénarios Métiers

### 1. Consultation externe
- L'usager accède à une application **Vue.js**.
- Il peut :
  - Scanner son **QR Code**
  - OU saisir :
    - Son **numéro de passeport**
    - Son **numéro de demande**

---

### 2. Transparence de l'historique
- L'application n'affiche **pas فقط le statut actuel**.
- Elle affiche **tout l'historique des changements d'état**, de manière chronologique :

Exemples :
- 📅 Date/Heure de création de la demande
- 📅 Date/Heure du scan terminé
- 📅 Date/Heure de validation / approbation
- 📅 Autres transitions de statut

---

## 📊 Données affichées à l'écran

### Historique
- Liste chronologique des étapes
- Triée par **date/heure croissante**
- Chaque entrée contient :
  - Statut
  - Date
  - Heure
  - (Optionnel) commentaire ou agent

---

## 🗄️ Tables concernées

- `historique_statut_demande`
- `demande`
- `demandeur`

---

## 🔗 Idée technique (API)

### Endpoint possible :