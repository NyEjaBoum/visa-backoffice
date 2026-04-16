
-- Reset all business tables (truncate in order to avoid FK issues)
SET search_path TO dev;

TRUNCATE TABLE 
	utilisateur,
	role_action,
	role,
	type_visa,
	type_demande_visa,
	statut
RESTART IDENTITY CASCADE;
