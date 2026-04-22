SET search_path TO dev;

-- Template des pieces justificatives par categorie (US03)
-- id_type_visa NULL = piece commune a toutes les categories
CREATE TABLE piece_type (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    id_type_visa INTEGER REFERENCES type_visa(id) ON DELETE CASCADE
);

-- Pieces communes (id_type_visa NULL)
INSERT INTO piece_type (libelle, id_type_visa) VALUES
('02 Photos d''identité', NULL),
('Notice de renseignement', NULL),
('Demande adressée au Ministère de l''Intérieur', NULL),
('Photocopie certifiée du passeport (1ère page)', NULL),
('Photocopie certifiée du visa en cours', NULL),
('Photocopie certifiée de la carte résident en cours de validité', NULL),
('Certificat de résidence à Madagascar', NULL),
('Extrait de casier judiciaire (moins de 3 mois)', NULL);

-- Pieces specifiques ETUDIANT (id=1)
INSERT INTO piece_type (libelle, id_type_visa) VALUES
('Certificat de scolarité ou inscription', 1),
('Justificatif de ressources (Bourse ou Prise en charge)', 1),
('Attestation d''hébergement', 1);

-- Pieces specifiques TRAVAILLEUR (id=2)
INSERT INTO piece_type (libelle, id_type_visa) VALUES
('Autorisation d''emploi (Ministère de la Fonction Publique)', 2),
('Attestation d''emploi délivrée par l''employeur', 2);

-- Pieces specifiques INVESTISSEUR (id=3)
INSERT INTO piece_type (libelle, id_type_visa) VALUES
('Statuts de la Société', 3),
('Extrait d''inscription au Registre de Commerce', 3),
('Carte Fiscale', 3);