SET search_path TO dev;

-- 1. Les Roles (ID fixes pour la securite)
INSERT INTO role (id, nom_role) VALUES
(1, 'ADMIN'),
(2, 'EMPLOYE')
ON CONFLICT (id) DO NOTHING;

-- 2. Les Statuts (ID fixes : indispensable pour la logique Java)
INSERT INTO statut (id, libelle) VALUES
(1, 'CREE'),
(10, 'VALIDE'),
(20, 'ANNULE')
ON CONFLICT (id) DO NOTHING;

-- 3. Les Types de Demande (ID fixes pour le selecteur Front)
INSERT INTO type_demande_visa (id, libelle) VALUES
(1, 'NOUVEAU TITRE'),
(2, 'TRANSFERT'),
(3, 'DUPLICATA')
ON CONFLICT (id) DO NOTHING;

-- 4. Les Categories de Visa (ID fixes pour la checklist dynamique)
INSERT INTO type_visa (id, libelle) VALUES
(1, 'ETUDIANT'),
(2, 'TRAVAILLEUR'),
(3, 'INVESTISSEUR')
ON CONFLICT (id) DO NOTHING;

-- 5. Les actions autorisees
INSERT INTO role_action (id_role, nom_action) VALUES
(1, 'SELECT'), (1, 'INSERT'), (1, 'UPDATE'), (1, 'DELETE'), (1, 'CREATE'),
(2, 'SELECT'), (2, 'INSERT'), (2, 'UPDATE')
ON CONFLICT DO NOTHING;

-- 6. Utilisateur de test (Admin lie a l'ID 1)
INSERT INTO utilisateur (identifiant, mdp, id_role) VALUES 
('admin', 'test', 1)
ON CONFLICT (identifiant) DO NOTHING;