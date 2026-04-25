--liquibase formatted sql

--changeset sprint1:demandeur labels:sprint1 comment:Table demandeur (anciennement individu)
CREATE TABLE IF NOT EXISTS demandeur (
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

--changeset sprint1:passeport labels:sprint1 comment:Table passeport
CREATE TABLE IF NOT EXISTS passeport (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL,
    id_demandeur INTEGER REFERENCES demandeur(id),
    date_delivrance DATE,
    date_expiration DATE
);

--changeset sprint1:visa-transformable labels:sprint1 comment:Visa transformable (preuve d'origine papier)
CREATE TABLE IF NOT EXISTS visa_transformable (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    id_passeport INTEGER REFERENCES passeport(id),
    id_demandeur INTEGER REFERENCES demandeur(id),
    date_entree DATE,
    lieu_entree VARCHAR(100),
    date_fin_visa DATE
);

--changeset sprint1:demande labels:sprint1 comment:Table demande (centrale)
CREATE TABLE IF NOT EXISTS demande (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    id_demandeur INTEGER REFERENCES demandeur(id),
    id_type_visa INTEGER REFERENCES type_visa(id),
    id_type_demande INTEGER REFERENCES type_demande(id),
    id_statut INTEGER REFERENCES statut(id),
    id_visa_transformable INTEGER REFERENCES visa_transformable(id),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
