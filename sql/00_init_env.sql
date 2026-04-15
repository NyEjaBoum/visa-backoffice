CREATE DATABASE visa_gestion;
\c visa_gestion;

-- Isolation des environnements
CREATE SCHEMA dev;
CREATE SCHEMA staging;
CREATE SCHEMA prod;

-- Rôles d'accès
CREATE ROLE app_dev LOGIN PASSWORD 'dev_pwd';
CREATE ROLE app_staging LOGIN PASSWORD 'staging_pwd';
CREATE ROLE app_prod LOGIN PASSWORD 'prod_pwd';

GRANT USAGE, CREATE ON SCHEMA dev, staging, prod TO app_dev;
ALTER ROLE app_dev SET search_path = dev;