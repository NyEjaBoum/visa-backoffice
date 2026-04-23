--liquibase formatted sql

--changeset sprint1:4 labels:sprint1 comment:Donnees initiales statuts
INSERT INTO statut (id, libelle) VALUES
    (1, 'CREE'),
    (2, 'VALIDE'),
    (3, 'ANNULE')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:5 labels:sprint1 comment:Donnees initiales types de demande
INSERT INTO type_demande_visa (id, libelle) VALUES
    (1, 'NOUVEAU TITRE'),
    (2, 'DUPLICATA'),
    (3, 'TRANSFERT')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:6 labels:sprint1 comment:Donnees initiales types de visa
INSERT INTO type_visa (id, libelle) VALUES
    (1, 'TRAVAILLEUR'),
    (2, 'INVESTISSEUR')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:7 labels:sprint1 comment:Donnees initiales situations familiales
INSERT INTO situation_familiale (id, libelle) VALUES
    (1, 'CELIBATAIRE'),
    (2, 'MARIE(E)'),
    (3, 'DIVORCE(E)'),
    (4, 'VEUF(VE)')
ON CONFLICT (id) DO NOTHING;

--changeset sprint1:8 labels:sprint1 comment:Donnees initiales nationalites
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
    ('BRESILIENNES'),
    ('MAURICIENNE'),
    ('COMORIENNE'),
    ('REUNIONNAISE'),
    ('AUTRE');

-- --changeset sprint1:9 labels:sprint1 comment:Utilisateur administrateur par defaut
-- INSERT INTO utilisateur (identifiant, mdp, role)
-- VALUES ('admin', '{noop}admin', 'OPERATEUR')
-- ON CONFLICT (identifiant) DO NOTHING;

-- --changeset sprint1:11 labels:sprint1 comment:Roles et actions par defaut
-- INSERT INTO role (code, libelle)
-- VALUES ('OPERATEUR', 'Opérateur')
-- ON CONFLICT (code) DO NOTHING;

-- INSERT INTO role_action (role_code, action) VALUES
--     ('OPERATEUR', 'SELECT'),
--     ('OPERATEUR', 'INSERT'),
--     ('OPERATEUR', 'UPDATE'),
--     ('OPERATEUR', 'DELETE'),
--     ('OPERATEUR', 'CREATE')
-- ON CONFLICT ON CONSTRAINT uk_role_action DO NOTHING;
