--liquibase formatted sql

--changeset sprint1:referentiels labels:sprint1 comment:Tables de reference (statut, type_demande, type_visa, situation_familiale, nationalite)
CREATE TABLE IF NOT EXISTS statut (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS type_demande (
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
