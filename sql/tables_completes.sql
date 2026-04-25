CREATE SCHEMA IF NOT EXISTS dev;
SET search_path TO dev;

-- ==========================================
-- 1. RÉFÉRENTIELS
-- ==========================================

CREATE TABLE statut (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL
);

CREATE TABLE type_demande (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL -- Nouveau titre, Duplicata, Transfert
);

CREATE TABLE type_visa (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL -- Travailleur, Investisseur
);

CREATE TABLE situation_familiale (
    id INTEGER PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

CREATE TABLE nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(100) NOT NULL
);

-- ==========================================
-- 2. MÉTIER PRINCIPAL
-- ==========================================

CREATE TABLE demandeur ( ---- devient demandeur
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

CREATE TABLE passeport (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL,
    id_demandeur INTEGER REFERENCES demandeur(id),
    date_delivrance DATE,
    date_expiration DATE
);

-- ==========================================
-- 3. VISA TRANSFORMABLE (HISTORIQUE PAPIER)
-- ==========================================

CREATE TABLE visa_transformable (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
    id_passeport INTEGER REFERENCES passeport(id),
    id_demandeur INTEGER REFERENCES demandeur(id),
    date_entree DATE,
    lieu_entree VARCHAR(100),
    date_fin_visa DATE
);

-- ==========================================
-- 4. DEMANDE (TABLE CENTRALE)
-- ==========================================

CREATE TABLE demande (
    id SERIAL PRIMARY KEY,
    numero VARCHAR(50) UNIQUE NOT NULL,
    id_demandeur INTEGER REFERENCES demandeur(id),
    id_type_visa INTEGER REFERENCES type_visa(id),
    id_type_demande INTEGER REFERENCES type_demande(id),
    id_statut INTEGER REFERENCES statut(id),
    id_visa_transformable INTEGER REFERENCES visa_transformable(id),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 5. HISTORIQUE
-- ==========================================

CREATE TABLE historique_statut_demande (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id) ON DELETE CASCADE,
    id_statut INTEGER REFERENCES statut(id),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 6. PIÈCES JUSTIFICATIVES
-- ==========================================

CREATE TABLE piece_type (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    id_type_visa INTEGER REFERENCES type_visa(id) ON DELETE CASCADE,
    est_obligatoire BOOLEAN DEFAULT TRUE
);

CREATE TABLE piece_justificative (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id),
    id_piece_type INTEGER REFERENCES piece_type(id),
    libelle_piece VARCHAR(255) NOT NULL,
    is_present BOOLEAN DEFAULT FALSE,
    chemin_fichier_scan TEXT,
    date_upload TIMESTAMP
);

-- ==========================================
-- 7. RÉSULTATS
-- ==========================================

CREATE TABLE visa (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER UNIQUE REFERENCES demande(id),
    numero VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INTEGER REFERENCES passeport(id)
);

CREATE TABLE carte_resident (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER UNIQUE REFERENCES demande(id),
    numero VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INTEGER REFERENCES passeport(id)
);