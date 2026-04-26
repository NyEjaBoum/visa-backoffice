-- 1. Se positionner sur le bon schéma
SET search_path TO dev;

-- 2. Supprimer la vue si elle existe (pour éviter les blocages de dépendances)
DROP VIEW IF EXISTS v_dossier_complet;

-- 3. Supprimer toutes les tables du métier et des référentiels
-- L'ordre importe peu avec CASCADE, mais on liste tout par sécurité
DROP TABLE IF EXISTS piece_fournie CASCADE;
DROP TABLE IF EXISTS piece_justificative CASCADE;
DROP TABLE IF EXISTS historique_statut_demande CASCADE;
DROP TABLE IF EXISTS visa CASCADE;
DROP TABLE IF EXISTS carte_resident CASCADE;
DROP TABLE IF EXISTS demande CASCADE;
DROP TABLE IF EXISTS visa_transformable CASCADE;
DROP TABLE IF EXISTS passeport CASCADE;
DROP TABLE IF EXISTS demandeur CASCADE;
DROP TABLE IF EXISTS statut CASCADE;
DROP TABLE IF EXISTS type_demande CASCADE;
DROP TABLE IF EXISTS type_visa CASCADE;
DROP TABLE IF EXISTS situation_familiale CASCADE;
DROP TABLE IF EXISTS nationalite CASCADE;

-- 4. Supprimer les tables de log de Liquibase (Le "cerveau" de Liquibase)
-- Cela force Liquibase à croire que la base est neuve
DROP TABLE IF EXISTS databasechangelog CASCADE;
DROP TABLE IF EXISTS databasechangeloglock CASCADE;

-- 5. Optionnel : Nettoyer les séquences si tu n'as pas utilisé SERIAL partout
-- DROP SEQUENCE IF EXISTS ...