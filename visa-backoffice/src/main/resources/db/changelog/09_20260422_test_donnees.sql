-- 09_20260422_test_donnees.sql
-- Données de test pour la base visa-backoffice

-- 1. Individus
INSERT INTO individu (nom, prenoms, date_naissance, nationalite, profession, adresse_mada, contact_mada)
VALUES 
('RAKOTO', 'Jean', '1990-01-01', 'Malgache', 'Ingénieur', 'Antananarivo', '0321234567'),
('RANDRIAM', 'Sofia', '1985-05-12', 'Française', 'Médecin', 'Fianarantsoa', '0341122334'),
('RAZAFI', 'Paul', '1978-09-23', 'Malgache', 'Comptable', 'Toamasina', '0339988776');

-- 2. Passeports
INSERT INTO passeport (id_individu, numero_pass, date_delivrance, date_expiration, is_active)
VALUES 
((SELECT id FROM individu WHERE nom='RAKOTO' AND prenoms='Jean'), 'A1234567', '2025-01-01', '2030-01-01', true),
((SELECT id FROM individu WHERE nom='RANDRIAM' AND prenoms='Sofia'), 'B7654321', '2024-03-15', '2029-03-15', true),
((SELECT id FROM individu WHERE nom='RAZAFI' AND prenoms='Paul'), 'C1122334', '2023-07-10', '2028-07-10', true);

-- 3. Visa transformable
INSERT INTO visa_transformable (id_individu, id_passeport, numero_reference, date_entree, lieu_entree, date_fin_visa)
VALUES 
((SELECT id FROM individu WHERE nom='RAKOTO' AND prenoms='Jean'), (SELECT id FROM passeport WHERE numero_pass='A1234567'), 'VT-2026-001', '2026-04-01', 'Ivato', '2026-10-01'),
((SELECT id FROM individu WHERE nom='RANDRIAM' AND prenoms='Sofia'), (SELECT id FROM passeport WHERE numero_pass='B7654321'), 'VT-2026-002', '2026-05-01', 'Ivato', '2026-11-01');

-- 4. Demandes de visa
INSERT INTO demande_visa (
    num_demande, id_individu, id_pass, id_type_demande, id_type_visa, id_statut, id_visa_transformable, date_creation
) VALUES 
('D-20260422-001', (SELECT id FROM individu WHERE nom='RAKOTO' AND prenoms='Jean'), (SELECT id FROM passeport WHERE numero_pass='A1234567'), 2, 1, 1, (SELECT id FROM visa_transformable WHERE numero_reference='VT-2026-001'), now()),
('D-20260422-002', (SELECT id FROM individu WHERE nom='RANDRIAM' AND prenoms='Sofia'), (SELECT id FROM passeport WHERE numero_pass='B7654321'), 2, 2, 1, (SELECT id FROM visa_transformable WHERE numero_reference='VT-2026-002'), now()),
('D-20260422-003', (SELECT id FROM individu WHERE nom='RAZAFI' AND prenoms='Paul'), (SELECT id FROM passeport WHERE numero_pass='C1122334'), 1, 3, 1, NULL, now());
-- 5. Pièces fournies
INSERT INTO piece_fournie (id_demande, libelle_piece, is_present)
VALUES 
((SELECT id FROM demande_visa WHERE num_demande='D-20260422-001'), 'Photo identité', true),
((SELECT id FROM demande_visa WHERE num_demande='D-20260422-001'), 'Passeport', true),
((SELECT id FROM demande_visa WHERE num_demande='D-20260422-002'), 'Photo identité', true),
((SELECT id FROM demande_visa WHERE num_demande='D-20260422-002'), 'Passeport', false),
((SELECT id FROM demande_visa WHERE num_demande='D-20260422-003'), 'Photo identité', true);
