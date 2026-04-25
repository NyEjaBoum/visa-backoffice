--liquibase formatted sql

--changeset sprint2:statut-visa-approuve labels:sprint2 comment:Statut VISA APPROUVE pour la demande d'injection (rattrapage)
INSERT INTO statut (id, libelle) VALUES
    (4, 'VISA APPROUVE')
ON CONFLICT (id) DO NOTHING;
