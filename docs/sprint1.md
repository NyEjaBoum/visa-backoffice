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


#### voici les donnees de piece justificative 

SET search_path TO dev;

-- Template des pieces justificatives par categorie (US03)
-- id_type_visa NULL = piece commune a toutes les categories
CREATE TABLE piece_justificative (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    id_type_visa INTEGER REFERENCES type_visa(id) ON DELETE CASCADE
);

-- Pieces communes (id_type_visa NULL)
INSERT INTO piece_justificative (libelle, id_type_visa) VALUES
('02 Photos d''identité', NULL),
('Notice de renseignement', NULL),
('Demande adressée au Ministère de l''Intérieur', NULL),
('Photocopie certifiée du passeport (1ère page)', NULL),
('Photocopie certifiée du visa en cours', NULL),
('Photocopie certifiée de la carte résident en cours de validité', NULL),
('Certificat de résidence à Madagascar', NULL),
('Extrait de casier judiciaire (moins de 3 mois)', NULL);

-- Pieces specifiques ETUDIANT (id=1)
INSERT INTO piece_justificative (libelle, id_type_visa) VALUES
('Certificat de scolarité ou inscription', 1),
('Justificatif de ressources (Bourse ou Prise en charge)', 1),
('Attestation d''hébergement', 1);

-- Pieces specifiques TRAVAILLEUR (id=2)
INSERT INTO piece_justificative (libelle, id_type_visa) VALUES
('Autorisation d''emploi (Ministère de la Fonction Publique)', 2),
('Attestation d''emploi délivrée par l''employeur', 2);

-- Pieces specifiques INVESTISSEUR (id=3)
INSERT INTO piece_justificative (libelle, id_type_visa) VALUES
('Statuts de la Société', 3),
('Extrait d''inscription au Registre de Commerce', 3),
('Carte Fiscale', 3);