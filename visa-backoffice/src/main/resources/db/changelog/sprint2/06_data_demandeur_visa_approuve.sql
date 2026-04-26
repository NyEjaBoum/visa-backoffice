--liquibase formatted sql

--changeset sprint2:data-demandeur-visa-approuve-final labels:sprint2 endDelimiter:$$ comment:Donnees de test corrigees sans libelle_piece
DO $$
DECLARE
    v_id_demandeur     INTEGER;
    v_id_passeport     INTEGER;
    v_id_visa_transfo  INTEGER;
    v_id_demande       INTEGER;
    v_id_nationalite   INTEGER;
    v_id_situation     INTEGER;
BEGIN
    -- Récupération des IDs de référence
    SELECT id INTO v_id_nationalite FROM nationalite WHERE libelle = 'FRANCAISE' LIMIT 1;
    SELECT id INTO v_id_situation   FROM situation_familiale WHERE libelle = 'MARIE(E)' LIMIT 1;

    -- 1. Demandeur
    INSERT INTO demandeur (
        nom, prenoms, date_naissance,
        id_situation_familiale, id_nationalite,
        profession, adresse_mada, contact_mada
    )
    VALUES (
        'MARTIN', 'Pierre Jean', '1985-03-15',
        v_id_situation, v_id_nationalite,
        'INGENIEUR', 'Lot IVB 42 Andohatapenaka, Antananarivo 101', '+261 34 00 111 22'
    )
    RETURNING id INTO v_id_demandeur;

    -- 2. Passeport
    INSERT INTO passeport (numero, id_demandeur, date_delivrance, date_expiration)
    VALUES ('FR-2019-123456', v_id_demandeur, '2019-06-10', '2029-06-09')
    RETURNING id INTO v_id_passeport;

    -- 3. Visa transformable
    INSERT INTO visa_transformable (
        numero, id_passeport, id_demandeur,
        date_entree, lieu_entree, date_fin_visa
    )
    VALUES (
        'VT-2022-00789', v_id_passeport, v_id_demandeur,
        '2022-01-15', 'IVATO', '2023-01-14'
    )
    RETURNING id INTO v_id_visa_transfo;

    -- 4. Demande (id_statut = 4 pour VISA APPROUVE)
    INSERT INTO demande (
        numero, id_demandeur,
        id_type_visa, id_type_demande,
        id_statut, id_visa_transformable
    )
    VALUES (
        'DEM-2022-00001', v_id_demandeur,
        1, 3,
        4, v_id_visa_transfo
    )
    RETURNING id INTO v_id_demande;

    -- 5. Historique
    INSERT INTO historique_statut_demande (id_demande, id_statut, date_changement) VALUES
        (v_id_demande, 1, NOW() - INTERVAL '30 days'),
        (v_id_demande, 4, NOW() - INTERVAL '5 days');

    -- 6. Visa & Carte Résident
    INSERT INTO visa (id_demande, numero, date_debut, date_fin, id_passeport)
    VALUES (v_id_demande, 'V-2023-00001', '2023-02-01', '2024-01-31', v_id_passeport);

    INSERT INTO carte_resident (id_demande, numero, date_debut, date_fin, id_passeport)
    VALUES (v_id_demande, 'CR-2023-00001', '2023-02-01', '2025-01-31', v_id_passeport);

    -- 7. Pièces fournies (Correction : pas de colonne libelle_piece)
    INSERT INTO piece_fournie (id_demande, id_piece_justificative, is_present, date_upload)
    SELECT v_id_demande, pj.id, TRUE, NOW() - INTERVAL '25 days'
    FROM piece_justificative pj
    WHERE pj.id_type_visa IS NULL OR pj.id_type_visa = 1;

END $$;