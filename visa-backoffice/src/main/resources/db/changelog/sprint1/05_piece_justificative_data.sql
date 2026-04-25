SET search_path TO dev;

-- Table de référence (Template)
CREATE TABLE IF NOT EXISTS piece_justificative (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    id_type_visa INTEGER REFERENCES type_visa(id) ON DELETE CASCADE,
    est_obligatoire BOOLEAN DEFAULT TRUE
);

-- Insertion des pièces communes
INSERT INTO piece_justificative (libelle, id_type_visa, est_obligatoire) VALUES
('02 Photos d''identité', NULL, TRUE),
('Notice de renseignement', NULL, TRUE),
('Demande adressée au Ministère de l''Intérieur', NULL, TRUE),
('Photocopie certifiée du passeport (1ère page)', NULL, TRUE),
('Photocopie certifiée du visa en cours', NULL, TRUE),
('Photocopie certifiée de la carte résident', NULL, FALSE), -- Optionnel par exemple
('Certificat de résidence à Madagascar', NULL, TRUE),
('Extrait de casier judiciaire (moins de 3 mois)', NULL, TRUE);


-- Insertion des pièces spécifiques TRAVAILLEUR
INSERT INTO piece_justificative (libelle, id_type_visa, est_obligatoire) VALUES
('Autorisation d''emploi', 1, TRUE),
('Attestation d''emploi', 1, TRUE);

-- Insertion des pièces spécifiques INVESTISSEUR
INSERT INTO piece_justificative (libelle, id_type_visa, est_obligatoire) VALUES
('Statuts de la Société', 2, TRUE),
('Extrait d''inscription au Registre de Commerce', 2, TRUE),
('Carte Fiscale', 2, TRUE);