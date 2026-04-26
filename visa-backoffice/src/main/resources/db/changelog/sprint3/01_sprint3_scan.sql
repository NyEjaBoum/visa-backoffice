--liquibase formatted sql

--changeset sprint3:statut-scan-termine labels:sprint3 comment:Nouveau statut SCAN TERMINÉ
INSERT INTO statut (id, libelle) VALUES
    (5, 'SCAN TERMINÉ')
ON CONFLICT (id) DO NOTHING;

--changeset sprint3:piece-fichier labels:sprint3 comment:Table piece_fichier pour les scans multi-fichiers par piece
CREATE TABLE IF NOT EXISTS piece_fichier (
    id SERIAL PRIMARY KEY,
    id_piece_fournie INTEGER NOT NULL REFERENCES piece_fournie(id) ON DELETE CASCADE,
    file_path TEXT NOT NULL,
    nom_original VARCHAR(255),
    date_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

--changeset sprint3:idx-piece-fichier labels:sprint3 comment:Index sur piece_fichier
CREATE INDEX IF NOT EXISTS idx_piece_fichier_id_piece_fournie ON piece_fichier(id_piece_fournie);
