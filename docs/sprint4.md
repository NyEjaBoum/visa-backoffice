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

### Endpoint possible

---

## 🚩 TODO Sprint 4 — Recherche et affichage demandes/historique

### 1. Recherche par numéro de demande

- Lorsqu'un usager saisit un **numéro de demande** :
  - Afficher la demande correspondante (mise en évidence).
  - Afficher la liste des autres demandes liées au même demandeur (même personne).
  - Afficher l'historique complet des statuts pour la demande sélectionnée (créé, scan terminé, approuvée, etc.).

### 2. Recherche par numéro de passeport

- Lorsqu'un usager saisit un **numéro de passeport** :
  - Afficher la liste de toutes les demandes liées à ce demandeur (ordre **décroissant** : les plus récentes en premier).
  - Pour chaque demande, permettre d'afficher l'historique des statuts (créé, scan terminé, approuvée, etc.).

---

### 📋 Tâches à réaliser

#### Backend (API Spring Boot)

- [ ] Créer/adapter un endpoint pour rechercher une demande par **numéro de demande** et retourner :
  - La demande principale
  - Les autres demandes du même demandeur
  - L'historique des statuts de la demande
- [ ] Créer/adapter un endpoint pour rechercher par **numéro de passeport** et retourner :
  - Toutes les demandes du demandeur (triées par date de création décroissante)
  - Pour chaque demande, l'historique des statuts
- [ ] Vérifier les jointures sur les tables `demandeur`, `demande`, `historique_statut_demande`

#### Frontend (Vue.js)

- [ ] Adapter la vue de suivi pour :
  - Permettre la saisie du numéro de demande ou du numéro de passeport
  - Afficher la demande principale et mettre en évidence la sélection
  - Afficher la liste des autres demandes liées
  - Afficher l'historique des statuts pour chaque demande
- [ ] Trier les demandes par date de création décroissante côté affichage

#### Base de données (si besoin)

- [ ] Vérifier la présence des index sur les colonnes utilisées pour la recherche (numéro de demande, numéro de passeport)

---

### 🛠️ Fichiers à corriger ou créer

- **Backend** :
  - Contrôleur API : `/visa-backoffice/src/main/java/com/visa/.../controller/` (ex: DemandeController.java)
  - Services : `/visa-backoffice/src/main/java/com/visa/.../service/`
  - Repositories : `/visa-backoffice/src/main/java/com/visa/.../repository/`
  - Entités si besoin
- **Frontend** :
  - Vue de suivi : `/front-office/src/views/SuiviView.vue`
  - Services API : `/front-office/src/services/api.js`
  - Composants d'affichage de l'historique
- **Base de données** :
  - Scripts SQL si modification structure
  - Vérification des index

---

### 📝 Remarques

- Bien valider le tri des demandes (date décroissante)
- Afficher l'historique complet pour chaque demande
- Mettre en évidence la demande recherchée
- Tester les cas où un demandeur a plusieurs demandes
