--liquibase formatted sql

--changeset sprint1:historique-statut labels:sprint1 comment:Historique des changements de statut
CREATE TABLE IF NOT EXISTS historique_statut_demande (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id) ON DELETE CASCADE,
    id_statut INTEGER REFERENCES statut(id),
    date_changement TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset sprint1:piece-type labels:sprint1 comment:Types de pieces justificatives
CREATE TABLE IF NOT EXISTS piece_type (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    id_type_visa INTEGER REFERENCES type_visa(id) ON DELETE CASCADE,
    est_obligatoire BOOLEAN DEFAULT TRUE
);

--changeset sprint1:piece-justificative labels:sprint1 comment:Pieces justificatives fournies
CREATE TABLE IF NOT EXISTS piece_justificative (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER REFERENCES demande(id),
    id_piece_type INTEGER REFERENCES piece_type(id),
    libelle_piece VARCHAR(255) NOT NULL,
    is_present BOOLEAN DEFAULT FALSE,
    chemin_fichier_scan TEXT,
    date_upload TIMESTAMP
);

--changeset sprint1:visa labels:sprint1 comment:Visa delivre (resultat)
CREATE TABLE IF NOT EXISTS visa (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER UNIQUE REFERENCES demande(id),
    numero VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INTEGER REFERENCES passeport(id)
);

--changeset sprint1:carte-resident labels:sprint1 comment:Carte de resident delivree (resultat)
CREATE TABLE IF NOT EXISTS carte_resident (
    id SERIAL PRIMARY KEY,
    id_demande INTEGER UNIQUE REFERENCES demande(id),
    numero VARCHAR(50),
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    id_passeport INTEGER REFERENCES passeport(id)
);
