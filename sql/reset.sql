
-- Reset all business tables (truncate in order to avoid FK issues)
SET search_path TO dev;

TRUNCATE TABLE 
	piece_fournie,
	carte_resident,
	visa,
	demande_visa,
	visa_transformable,
	passeport,
	individu,
	utilisateur,
	role_action,
	role,
	situation_familiale,
	type_visa,
	type_demande_visa,
	statut
RESTART IDENTITY CASCADE;

TRUNCATE TABLE DATABASECHANGELOG, DATABASECHANGELOGLOCK RESTART IDENTITY CASCADE;