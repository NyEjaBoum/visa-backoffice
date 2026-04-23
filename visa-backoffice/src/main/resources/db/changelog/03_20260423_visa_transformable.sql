--liquibase formatted sql

--changeset sprint2:1 labels:sprint2 comment:Ajout table visa_transformable
CREATE TABLE IF NOT EXISTS visa_transformable (
    id SERIAL PRIMARY KEY,
    id_individu  INTEGER REFERENCES individu(id),
    id_passeport INTEGER REFERENCES passeport(id),
    reference VARCHAR(50) NOT NULL UNIQUE,
    date_entree DATE,
    lieu_entree VARCHAR(100),
    date_fin_visa DATE
);

--changeset sprint2:2 labels:sprint2 comment:Ajout lien demande_visa -> visa_transformable
ALTER TABLE demande_visa
    ADD COLUMN IF NOT EXISTS id_visa_transformable INTEGER;

--changeset sprint2:3 labels:sprint2 comment:Ajout contrainte FK demande_visa -> visa_transformable
--preconditions onFail:MARK_RAN onError:HALT
--precondition-sql-check expectedResult:0 SELECT COUNT(1) FROM pg_constraint WHERE conname = 'fk_demande_visa_vt';

ALTER TABLE demande_visa
    ADD CONSTRAINT fk_demande_visa_vt
    FOREIGN KEY (id_visa_transformable) REFERENCES visa_transformable(id);
