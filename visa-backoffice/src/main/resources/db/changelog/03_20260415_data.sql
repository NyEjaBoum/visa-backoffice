SET search_path TO dev;

-- Création d'un ADMIN (Mdp: password123)
INSERT INTO utilisateur (identifiant, mdp, id_role) VALUES ('admin', 'test', 1);

-- Attribution des actions autorisées pour le rôle ADMIN (ID 1)
INSERT INTO role_action (id_role, nom_action) VALUES 
(1, 'SELECT'), (1, 'INSERT'), (1, 'UPDATE'), (1, 'DELETE'), (1, 'CREATE');

-- Attribution des actions pour le rôle EMPLOYE (ID 2)
INSERT INTO role_action (id_role, nom_action) VALUES 
(2, 'SELECT'), (2, 'INSERT'), (2, 'UPDATE');