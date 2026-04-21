SET search_path TO dev;

-- Backfill sur les colonnes historiques date_debut/date_fin a partir des nouvelles colonnes
UPDATE visa_transformable
SET
    date_debut = COALESCE(date_debut, date_entree),
    date_fin   = COALESCE(date_fin, date_fin_visa)
WHERE (date_debut IS NULL AND date_entree IS NOT NULL)
   OR (date_fin IS NULL AND date_fin_visa IS NOT NULL);
