-- Réinitialisation complète de la base visa_gestion (schéma dev uniquement)
-- À utiliser uniquement en développement !

-- Supprimer les tables de suivi Liquibase
DROP TABLE IF EXISTS dev.databasechangelog CASCADE;
DROP TABLE IF EXISTS dev.databasechangeloglock CASCADE;

-- Supprimer toutes les tables métier (ajoute ici toutes tes tables)
DROP TABLE IF EXISTS dev.piece_fournie CASCADE;
DROP TABLE IF EXISTS dev.demande_visa CASCADE;
DROP TABLE IF EXISTS dev.passeport CASCADE;
DROP TABLE IF EXISTS dev.individu CASCADE;
DROP TABLE IF EXISTS dev.utilisateur CASCADE;
DROP TABLE IF EXISTS dev.role_action CASCADE;
DROP TABLE IF EXISTS dev.role CASCADE;
DROP TABLE IF EXISTS dev.type_visa CASCADE;
DROP TABLE IF EXISTS dev.type_demande_visa CASCADE;
DROP TABLE IF EXISTS dev.statut CASCADE;

-- Tu peux ajouter d'autres DROP TABLE si tu ajoutes de nouvelles tables

-- Optionnel : recréer le schéma dev si besoin
-- DROP SCHEMA IF EXISTS dev CASCADE;
-- CREATE SCHEMA dev;
