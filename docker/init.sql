-- Le user et la DB sont déjà créés par les variables d'env Docker
CREATE SCHEMA IF NOT EXISTS dev;
CREATE SCHEMA IF NOT EXISTS staging;
CREATE SCHEMA IF NOT EXISTS prod;

GRANT USAGE, CREATE ON SCHEMA dev, staging, prod TO app_dev;
ALTER ROLE app_dev SET search_path = dev;
