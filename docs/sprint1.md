# 🚀 SPRINT 1 : Fondations & Nouveau Titre (15/04/2026)

## Objectif

Mettre en place l'authentification sécurisée et le formulaire de création de dossier pour une première demande de transformation de visa.

---

### [ ] US01 : Authentification & Autorisations (Login)

#### Développeur : 3657

* **Écran** : Page de connexion login.
* **Scénario** : L'agent saisit son identifiant et son mot de passe. En cas de succès, un Token JWT est généré contenant l'identifiant de l'agent, son rôle et la liste de ses permissions (actions autorisées, ex : SELECT, INSERT, UPDATE, DELETE, CREATE). Ce token est utilisé pour sécuriser toutes les requêtes ultérieures.
* **Métier** : Lors de la connexion, le back-end extrait le rôle de l'utilisateur et toutes ses actions autorisées depuis la table `role_action`. Ces permissions sont placées dans le token JWT. Les tables de nomenclatures (`statut`, `type_visa`, `type_demande_visa`) sont chargées une seule fois en session côté client après la connexion (elles ne sont pas dans le token).
 À chaque action, le système vérifie en session si l'utilisateur a le droit d'effectuer l'opération demandée, selon ses permissions extraites au login.
* **Base** : `utilisateur`, `role`, `role_action`.

---

### [ ] US02 : Création de Dossier (Configuration & Nouveau Titre)

#### Développeuse : 3389

* **Écran** : Formulaire organisé en 3 blocs : État Civil, Voyage (Passeport/Visa) et Catégorie. Un sélecteur en haut définit la nature de la démarche (Type de Demande).
* **Scénario** : L'agent saisit les informations d'une première demande de transformation. La référence du visa transformable est un champ obligatoire.
* **Métier** : Validation des dates (le passeport ne doit pas être expiré). Génération automatique du numéro de dossier unique. Initialisation automatique du statut à **1 (CRÉÉ)**.
* **Base** : `type_demande_visa`, `individu`, `passeport`, `demande_visa`.

---

### [ ] US03 : Gestion des Catégories & Checklist

#### Développeurs : 3389 (Back) & 3657 (Front)

* **Écran** : Menu déroulant pour choisir le Type de Visa (Investisseur / Travailleur). Une liste de cases à cocher (checklist) apparaît dynamiquement selon ce choix.
* **Scénario** : L'agent vérifie physiquement les documents apportés par l'étranger et coche les cases correspondantes dans l'interface.
* **Métier** : Récupération des types de visa. Vérification de la présence de toutes les pièces obligatoires avant de permettre l'enregistrement final en base.
* **Base** : `type_visa`, `piece_fournie`.

---

### 📝 NOTE SUR LE CAS "SANS DONNÉES ANTÉRIEURES"

Lors d'un duplicata futur, si l'individu n'existe pas encore en base de données (ancien dossier papier), le système permet de créer manuellement l'entrée dans les tables `individu` et `passeport` directement pendant la saisie de la demande. Cela permet de régulariser et numériser les archives sans bloquer le processus administratif.

### 🎨 Consigne UI (Pour 3657)

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
