SET search_path TO dev;

ALTER TABLE visa_transformable
    DROP COLUMN IF EXISTS date_debut,
    DROP COLUMN IF EXISTS date_fin;
