-- Réinitialisation complète de la base visa_gestion (schéma dev uniquement)
-- À utiliser uniquement en développement !

DROP TABLE IF EXISTS dev.databasechangelog CASCADE;
DROP TABLE IF EXISTS dev.databasechangeloglock CASCADE;


-- Tables "feuilles" (sans dépendants)
DROP TABLE IF EXISTS dev.piece_fournie CASCADE;
DROP TABLE IF EXISTS dev.carte_resident CASCADE;
DROP TABLE IF EXISTS dev.visa CASCADE;
DROP TABLE IF EXISTS dev.piece_type CASCADE;

-- Tables "liées"
DROP TABLE IF EXISTS dev.demande_visa CASCADE;
DROP TABLE IF EXISTS dev.visa_transformable CASCADE;
DROP TABLE IF EXISTS dev.passeport CASCADE;
DROP TABLE IF EXISTS dev.individu CASCADE;

-- Référentiels et utilisateurs
DROP TABLE IF EXISTS dev.utilisateur CASCADE;
DROP TABLE IF EXISTS dev.role_action CASCADE;
DROP TABLE IF EXISTS dev.role CASCADE;
DROP TABLE IF EXISTS dev.situation_familiale CASCADE;
DROP TABLE IF EXISTS dev.type_visa CASCADE;
DROP TABLE IF EXISTS dev.type_demande_visa CASCADE;
DROP TABLE IF EXISTS dev.statut CASCADE;


-- Tables "feuilles" (sans dépendants)
DROP TABLE IF EXISTS dev.piece_fournie CASCADE;
DROP TABLE IF EXISTS dev.carte_resident CASCADE;
DROP TABLE IF EXISTS dev.visa CASCADE;

-- Tables "liées"
DROP TABLE IF EXISTS dev.demande_visa CASCADE;
DROP TABLE IF EXISTS dev.visa_transformable CASCADE;
DROP TABLE IF EXISTS dev.passeport CASCADE;
DROP TABLE IF EXISTS dev.individu CASCADE;

-- Référentiels et utilisateurs
DROP TABLE IF EXISTS dev.utilisateur CASCADE;
DROP TABLE IF EXISTS dev.role_action CASCADE;
DROP TABLE IF EXISTS dev.role CASCADE;
DROP TABLE IF EXISTS dev.situation_familiale CASCADE;
DROP TABLE IF EXISTS dev.type_visa CASCADE;
DROP TABLE IF EXISTS dev.type_demande_visa CASCADE;
DROP TABLE IF EXISTS dev.statut CASCADE;

-- Tu peux ajouter d'autres DROP TABLE si tu ajoutes de nouvelles tables

-- Optionnel : recréer le schéma dev si besoin
-- DROP SCHEMA IF EXISTS dev CASCADE;
-- CREATE SCHEMA dev;
