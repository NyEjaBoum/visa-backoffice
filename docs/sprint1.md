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

S'inspirer de `templates/html/demande.html` pour la structure des blocs, les champs  et la checklist dynamique (style SaaS, clean, rounded-corners).
