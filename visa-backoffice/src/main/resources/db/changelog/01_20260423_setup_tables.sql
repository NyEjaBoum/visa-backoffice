--liquibase formatted sql

--changeset sprint1:1 labels:sprint1 comment:Referentiels de base
CREATE TABLE IF NOT EXISTS statut (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS type_demande_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS type_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS situation_familiale (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

--changeset sprint1:2 labels:sprint1 comment:Tables metier principales
CREATE TABLE IF NOT EXISTS individu (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(255),
    nom_jeune_fille VARCHAR(100),
    date_naissance DATE NOT NULL,
    id_situation_familiale INTEGER REFERENCES situation_familiale(id),
    id_nationalite INTEGER REFERENCES nationalite(id),
    profession VARCHAR(100),
    adresse_mada TEXT NOT NULL,
    contact_mada VARCHAR(100),
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS passeport (
    id SERIAL PRIMARY KEY,
    id_individu INTEGER REFERENCES individu(id),
    numero VARCHAR(50) NOT NULL,
    date_delivrance DATE,
    date_expiration DATE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS demande_visa (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    id_individu INTEGER REFERENCES individu(id),
    id_pass INTEGER REFERENCES passeport(id),
    id_type_visa INTEGER REFERENCES type_visa(id),
    id_type_demande INTEGER REFERENCES type_demande_visa(id),
    id_statut INTEGER REFERENCES statut(id),
    qr_code_token TEXT UNIQUE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- --changeset sprint1:3 labels:sprint1 comment:Table utilisateurs pour authentification
-- CREATE TABLE IF NOT EXISTS utilisateur (
--     id SERIAL PRIMARY KEY,
--     identifiant VARCHAR(100) NOT NULL UNIQUE,
--     mdp VARCHAR(255) NOT NULL,
--     role VARCHAR(50) NOT NULL DEFAULT 'OPERATEUR'
-- );

-- --changeset sprint1:10 labels:sprint1 comment:Tables roles et permissions
-- CREATE TABLE IF NOT EXISTS role (
--     code VARCHAR(50) PRIMARY KEY,
--     libelle VARCHAR(100) NOT NULL
-- );

-- CREATE TABLE IF NOT EXISTS role_action (
--     id SERIAL PRIMARY KEY,
--     role_code VARCHAR(50) NOT NULL REFERENCES role(code) ON DELETE CASCADE,
--     action VARCHAR(50) NOT NULL,
--     CONSTRAINT uk_role_action UNIQUE (role_code, action)
-- );
