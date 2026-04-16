# Database Schema

## 1. Database Schema Reference

### statut

id (PK) : 1 (Créé), 11 (Validé), 21 (Annulé).
libelle : Le nom de l'état du dossier.

## type_demande_visa

id (PK) : 1 (Nouveau Titre), 2 (Transfert), 3 (Duplicata).
libelle : Nature de la procédure (ex: "Transformation de visa").
type_visa
id (PK) : 1 (Étudiant), 2 (Travailleur), 3 (Investisseur).
libelle : Catégorie professionnelle ou sociale de l'étranger.

role
id (PK)
nom_role : ADMIN, EMPLOYE.
role_action
id_role (FK vers role)
nom_action : Permissions incluses dans le Token JWT (ex: "CREATE, DELETE,INSERT,SELECT,UPDATE").

## 2. TABLES DE DONNÉES (PERSISTANCE)

## utilisateur

id (PK)
identifiant / mdp
id_role (FK vers role)
individu
id (PK)
nom, prenoms, nom_jeune_fille
date_naissance, situation_familiale, nationalite, profession
adresse_mada, contact_mada (Téléphone/Email)

## passeport

id (PK)
id_individu (FK vers individu)
numero_pass
date_delivrance, date_expiration
is_active (Boolean) : Indique si c'est le passeport actuel.

## demande_visa

id (PK)
num_demande : Numéro de dossier généré (ex: MADA-2026-0001).
id_individu (FK vers individu)
id_pass (FK vers passeport)
id_type_visa (FK vers type_visa)
id_type_demande (FK vers type_demande_visa)
id_statut (FK vers statut)
ref_visa_trans : Référence obligatoire du visa transformable.
date_entree, lieu_entree, date_fin_visa
date_creation (Timestamp)

## piece_fournie

id (PK)
id_demande (FK vers demande_visa)
libelle_piece : Nom du document (ex: "Casier Judiciaire").
is_present (Boolean) : État de la case à cocher.
