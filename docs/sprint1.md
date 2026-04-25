# SPRINT_1.md
## Saisie de Demande et Recevabilité

### User Story
En tant qu'opérateur, je veux enregistrer un demandeur et son visa transformable afin d’initier son premier dossier dans le système.

---

### Scénarios Métiers

#### 1. Demande Standard (Nouveau Titre)
- L’opérateur saisit :
  - État civil du demandeur (demandeur)
  - Informations du passeport
  - Informations du visa transformable (obligatoire)
- Il sélectionne :
  - Type de visa (Travailleur / Investisseur)
- Il coche les pièces justificatives reçues

---

#### 2. Contrôle de Recevabilité
Avant validation, le système vérifie que les champs obligatoires sont remplis :

- Nom
- Date de naissance
- Nationalité
- Adresse à Madagascar

Si un champ est manquant → refus d’enregistrement

---

#### 3. Statut Initial
La demande est créée avec le statut :

CREER

---

#### 4. Flexibilité (Modification)
Tant que la demande est au statut CREER :
- Toutes les informations sont modifiables :
  - Numéro de passeport
  - Adresse
  - Infos personnelles
  - Pièces justificatives

---

### Données Utilisées

#### individu devient demandeur 
- Nom
- Prénoms
- Date de naissance
- Situation familiale (FK)
- Nationalité (FK)
- Adresse Madagascar

#### Passeport
- Numéro
- Date de délivrance
- Date d’expiration

#### Visa Transformable
- Numéro
- Date d’entrée
- Lieu d’entrée

#### Demande
- Type de visa
- Type de demande : Nouveau Titre
- Statut : CREER

---

### Tables Sollicitées
- demandeur
- passeport
- visa_transformable
- demande
- piece_justificative