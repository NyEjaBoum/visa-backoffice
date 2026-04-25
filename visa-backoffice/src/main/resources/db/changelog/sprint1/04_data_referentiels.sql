--liquibase formatted sql

--changeset sprint1:data-statuts labels:sprint1 comment:Statuts initiaux Sprint 1
INSERT INTO statut (id, libelle) VALUES
    (1, 'CREE'),
    (2, 'VALIDE'),
    (3, 'ANNULE')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:data-types-demande labels:sprint1 comment:Types de demande
INSERT INTO type_demande (id, libelle) VALUES
    (1, 'NOUVEAU TITRE'),
    (2, 'DUPLICATA'),
    (3, 'TRANSFERT')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:data-types-visa labels:sprint1 comment:Types de visa
INSERT INTO type_visa (id, libelle) VALUES
    (1, 'TRAVAILLEUR'),
    (2, 'INVESTISSEUR')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:data-situations-familiales labels:sprint1 comment:Situations familiales
INSERT INTO situation_familiale (id, libelle) VALUES
    (1, 'CELIBATAIRE'),
    (2, 'MARIE(E)'),
    (3, 'DIVORCE(E)'),
    (4, 'VEUF(VE)')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:data-nationalites labels:sprint1 comment:Nationalites de base
INSERT INTO nationalite (libelle) VALUES
    ('FRANCAISE'),
    ('MALGACHE'),
    ('AMERICAINE'),
    ('BRITANNIQUE'),
    ('CHINOISE'),
    ('INDIENNE'),
    ('ITALIENNE'),
    ('ESPAGNOLE'),
    ('ALLEMANDE'),
    ('PORTUGAISE'),
    ('JAPONAISE'),
    ('CANADIENNE'),
    ('AUSTRALIENNE'),
    ('BRESILIENNE'),
    ('MAURICIENNE'),
    ('COMORIENNE'),
    ('REUNIONNAISE'),
    ('AUTRE');
