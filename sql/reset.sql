-- Réinitialisation complète de la base visa_gestion (schéma dev uniquement)
-- À utiliser uniquement en développement !


-- Vider toutes les tables métier (TRUNCATE)
TRUNCATE TABLE 
	dev.piece_fournie,
	dev.demande_visa,
	dev.passeport,
	dev.individu,
	dev.utilisateur,
	dev.role_action,
	dev.role,
	dev.type_visa,
	dev.type_demande_visa,
	dev.statut
RESTART IDENTITY CASCADE;

-- Tu peux ajouter d'autres DROP TABLE si tu ajoutes de nouvelles tables

-- Optionnel : recréer le schéma dev si besoin
-- DROP SCHEMA IF EXISTS dev CASCADE;
-- CREATE SCHEMA dev;
