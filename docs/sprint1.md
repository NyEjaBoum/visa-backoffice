# Spécifications Fonctionnelles - Application Visa

## US00 : Authentification & Autorisations (Login)
**Développeur** : 3657

* **Écran** : Page de connexion (Login).
* **Scénario** : L'agent saisit son identifiant et son mot de passe. En cas de succès, un **Token JWT** est généré contenant :
    * L'identifiant de l'agent.
    * Son rôle.
    * La liste de ses permissions (ex: `SELECT`, `INSERT`, `UPDATE`, `DELETE`).
* **Logique Métier** : 
    * Le back-end extrait les permissions depuis la table `role_action`.
    * Les tables de nomenclature (`statut`, `type_visa`, `type_demande_visa`) sont chargées en cache/session côté client après connexion.
    * À chaque action, le système vérifie si le token autorise l'opération.
* **Base de données** : `utilisateur`, `role`, `role_action`.

---

## Sprint 1 : Saisie de Demande et Recevabilité

**User Story** : *"En tant qu'opérateur, je veux saisir les informations d'un individu et de son passeport pour insérer une demande de visa."*

### Scénarios Métiers :
1.  **Saisie Initiale** : 
    * L'opérateur remplit les champs obligatoires (Individu, Passeport).
    * Il saisit les informations du **Visa Transformable** (justificatif d'entrée).
    * Il sélectionne le type de visa (Travailleur/Investisseur) et valide les pièces présentes.
2.  **Flexibilité (Statut CRÉÉ)** : 
    * Tant que la demande est au statut `CRÉÉ`, l'opérateur peut modifier toutes les infos (erreur de saisie, contact, etc.).
3.  **Génération du Suivi** : 
    * Le système génère un **QR Code unique** (token de suivi) dès l'insertion pour l'usager.

### Données à l'écran :
* **Individu** : Nom (Obligatoire), Prénoms, Date de naissance (Obligatoire), Adresse Madagascar (Obligatoire), Contact.
* **Référentiels** : Nationalité, Situation Familiale.
* **Passeport** : Numéro, Date de délivrance, Date d'expiration.
* **Visa Transformable** : Référence, Date d'entrée, Lieu d'entrée.
* **Demande** : Type de visa (Travailleur/Investisseur) et Type de demande (Nouveau titre).

**Tables concernées** : `individu`, `passeport`, `visa_transformable`, `demande_visa`, `nationalite`, `situation_familiale`, `type_visa`, `type_demande_visa`.

---

## Sprint 2 : Gestion des Antécédents (Duplicata & Transfert)

**User Story** : *"En tant qu'opérateur, je veux traiter les demandes de duplicata ou de transfert, même si l'usager n'est pas dans notre base."*

### Scénarios Métiers :
1.  **Cas Normal (Déjà en base)** : 
    * Recherche par numéro de passeport ou demande.
    * Le système récupère les infos. L'opérateur crée une demande de **Duplicata** ou **Transfert**.
    * *Note : Le champ `id_visa_transformable` dans la table `demande_visa` reste **NULL** pour ces cas.*
2.  **Cas "Sans Données Antérieures" (Régularisation)** :
    * L'usager a un titre physique mais n'existe pas dans le logiciel.
    * **Action** : On crée une demande de "Nouveau Titre" (Injection) avec les infos du `visa_transformable` papier pour créer son dossier numérique.
    * Une fois validé (VISA APPROUVÉ), on peut traiter son Duplicata/Transfert.
3.  **Édition** : 
    * Génération du PDF "Attestation récépissé dossier" (État civil, infos passeport, visa actuel et liste des documents reçus).

### Données à l'écran :
* **Recherche** : Numéro de passeport ou ID demande.
* **Historique papier** : Table `visa_transformable` (pour le cas de régularisation).

**Tables concernées** : `visa_transformable`, `visa`, `carte_resident`, `demande_visa`, `statut`.