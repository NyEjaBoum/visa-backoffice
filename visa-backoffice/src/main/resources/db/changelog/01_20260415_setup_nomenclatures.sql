SET search_path TO dev;

-- Statuts du dossier
CREATE TABLE statut (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

-- Nature de la procedure
CREATE TABLE type_demande_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- Categorie de l'etranger
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