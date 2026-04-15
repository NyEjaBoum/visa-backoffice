SET search_path TO dev;

-- Statuts du dossier
CREATE TABLE statut (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

-- Nature de la procédure
CREATE TABLE type_demande_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Catégorie de l'étranger
CREATE TABLE type_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Profils et Droits (JWT)
CREATE TABLE role (
    id SERIAL PRIMARY KEY,
    nom_role VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE role_action (
    id SERIAL PRIMARY KEY,
    id_role INTEGER REFERENCES role(id) ON DELETE CASCADE,
    nom_action VARCHAR(50) NOT NULL -- ex: 'CREATE', 'INSERT', etc.
);

-- Remplissage initial
INSERT INTO statut (id, libelle) VALUES (1, 'Cree'), (11, 'Valide'), (21, 'Annule');
INSERT INTO type_demande_visa (id, libelle) VALUES (1, 'Nouveau Titre'), (2, 'Transfert'), (3, 'Duplicata');
INSERT INTO type_visa (id, libelle) VALUES (1, 'Etudiant'), (2, 'Travailleur'), (3, 'Investisseur');
INSERT INTO role (nom_role) VALUES ('ADMIN'), ('EMPLOYE');