# SPRINT_2.md
## Gestion des Antécédents (Cas Complexe)

### User Story
En tant qu'opérateur, je veux traiter les demandes de duplicata ou de transfert, même pour des usagers absents de la base mais possédant des titres valides.

---

### Scénarios Métiers

#### 1. Cas Normal (Données présentes)
- Recherche via :
  - Numéro de carte de résident
  - Numéro de passeport

Si trouvé :
- Récupération du demandeur existant
- Création d’une nouvelle demande :
  - Type : Duplicata ou Transfert
- Liaison avec :
  - Dernier visa_transformable

---

#### 2. Cas Problématique (Rattrapage sans données)
L’usager :
- Possède une carte valide (ex: jusqu’en 2030)
- Mais n’existe pas en base

---

### Saisie de Rattrapage
L’opérateur saisit :
- Demandeur
- Passeport
- Informations de la carte actuelle (via visa_transformable)

---

### Action : Sauvegarde (Double Demande)

Le système crée automatiquement 2 demandes :

---

#### Demande A — Injection
- Type : Nouveau Titre
- Statut : VISA APPROUVE

Effet :
- Génération immédiate de :
  - visa
  - carte_resident

Le demandeur devient existant dans le système

---

#### Demande B — Demande Cible
- Type : Duplicata ou Transfert
- Statut : CREER

Cette demande :
- Est liée au même id_demandeur
- Réutilise le même id_visa_transformable

---

### Règle de Liaison
- Le visa_transformable sert de preuve d’origine
- Il est partagé entre :
  - Demande A
  - Demande B

---

### Édition du Récépissé
Une fois les deux demandes créées :
- Le bouton "Imprimer Récépissé" devient disponible

Le PDF contient :
- Informations du demandeur
- Informations du passeport
- Liste des pièces justificatives

---

### Données Utilisées

#### Recherche
- Numéro de carte résident
- Numéro de passeport

#### Liaison
- id_demandeur
- id_visa_transformable

---

### Tables Sollicitées
- demande (x2)
- visa
- carte_resident
- visa_transformable
- historique_statut_demande