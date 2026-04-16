SET search_path TO dev;

CREATE TABLE utilisateur (
    id SERIAL PRIMARY KEY,
    identifiant VARCHAR(100) UNIQUE NOT NULL,
    mdp TEXT NOT NULL,
    id_role INTEGER REFERENCES role(id)
);

CREATE TABLE individu (
    id SERIAL PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenoms VARCHAR(255),
    nom_jeune_fille VARCHAR(100),
    date_naissance DATE NOT NULL,
    situation_familiale VARCHAR(50),
    nationalite VARCHAR(100),
    profession VARCHAR(100),
    adresse_mada TEXT,
    contact_mada VARCHAR(100)
);

CREATE TABLE passeport (
    id SERIAL PRIMARY KEY,
    id_individu INTEGER REFERENCES individu(id),
    numero_pass VARCHAR(50) NOT NULL,
    date_delivrance DATE,
    date_expiration DATE,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE TABLE demande_visa (
    id SERIAL PRIMARY KEY,
    num_demande VARCHAR(50) UNIQUE NOT NULL,
    id_individu INTEGER REFERENCES individu(id),
    id_pass INTEGER REFERENCES passeport(id),
    id_type_visa INTEGER REFERENCES type_visa(id),
    id_type_demande INTEGER REFERENCES type_demande_visa(id),
    id_statut INTEGER REFERENCES statut(id),
    ref_visa_trans VARCHAR(100) NOT NULL,
    date_entree DATE,
    lieu_entree VARCHAR(100),
    date_fin_visa DATE,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE piece_fournie (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande_visa(id),
    libelle_piece VARCHAR(255) NOT NULL,
    is_present BOOLEAN DEFAULT FALSE
);