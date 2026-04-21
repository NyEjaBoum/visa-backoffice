SET search_path TO dev;

-- Deplacer les informations "date/lieu d'entree" et "fin du visa actuel" vers visa_transformable
ALTER TABLE visa_transformable
    ADD COLUMN date_entree DATE,
    ADD COLUMN lieu_entree VARCHAR(100),
    ADD COLUMN date_fin_visa DATE;

-- Migrer les donnees existantes depuis demande_visa (si presentes)
UPDATE visa_transformable vt
SET
    date_entree  = d.date_entree,
    lieu_entree  = d.lieu_entree,
    date_fin_visa = d.date_fin_visa
FROM demande_visa d
WHERE d.id_visa_transformable = vt.id;

ALTER TABLE demande_visa
    DROP COLUMN date_entree,
    DROP COLUMN lieu_entree,
    DROP COLUMN date_fin_visa;
