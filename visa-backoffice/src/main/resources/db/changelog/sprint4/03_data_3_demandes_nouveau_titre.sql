--liquibase formatted sql

--changeset sprint4:data-3-demandes-nouveau-titre labels:sprint4 splitStatements:false endDelimiter:$$ comment:3 demandes de test NOUVEAU TITRE (1 incomplete, 2 completes avec scans photo)
DO $$
DECLARE
    v_nat_fr       INTEGER;
    v_nat_mg       INTEGER;
    v_sit_cel      INTEGER;
    v_sit_mar      INTEGER;

    v_demandeur_id INTEGER;
    v_passeport_id INTEGER;
    v_vt_id        INTEGER;
    v_demande_id   INTEGER;

    v_type_demande_nouveau INTEGER := 1; -- NOUVEAU TITRE
    v_type_visa_travailleur INTEGER := 1;
    v_type_visa_investisseur INTEGER := 2;

    v_statut_cree INTEGER := 1;
    v_statut_visa_approuve INTEGER := 4;
    v_statut_scan_termine INTEGER := 5;

    v_qr UUID;
BEGIN
    -- Références
    SELECT id INTO v_nat_fr FROM nationalite WHERE libelle = 'FRANCAISE' LIMIT 1;
    SELECT id INTO v_nat_mg FROM nationalite WHERE libelle = 'MALGACHE' LIMIT 1;
    SELECT id INTO v_sit_cel FROM situation_familiale WHERE libelle = 'CELIBATAIRE' LIMIT 1;
    SELECT id INTO v_sit_mar FROM situation_familiale WHERE libelle = 'MARIE(E)' LIMIT 1;

    -- =====================================================================
    -- DEMANDE #1 — CREE — pièces incomplètes (pas toutes cochées)
    -- =====================================================================
    INSERT INTO demandeur (
        nom, prenoms, date_naissance,
        id_situation_familiale, id_nationalite,
        profession, adresse_mada, contact_mada
    ) VALUES (
        'RAKOTO', 'Aina', '1994-07-21',
        v_sit_cel, v_nat_mg,
        'ETUDIANTE', 'Ambatonakanga, Antananarivo 101', '+261 34 11 222 33'
    ) RETURNING id INTO v_demandeur_id;

    INSERT INTO passeport (numero, id_demandeur, date_delivrance, date_expiration)
    VALUES ('MG-2021-000111', v_demandeur_id, '2021-05-10', '2031-05-09')
    RETURNING id INTO v_passeport_id;

    INSERT INTO visa_transformable (
        numero, id_passeport, id_demandeur,
        date_entree, lieu_entree, date_fin_visa
    ) VALUES (
        'VT-2026-NT-0001', v_passeport_id, v_demandeur_id,
        '2026-02-10', 'IVATO', '2026-05-09'
    ) RETURNING id INTO v_vt_id;

    v_qr := (
        substr(md5(random()::text || clock_timestamp()::text), 1, 8) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 9, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 13, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 17, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 21, 12)
    )::uuid;

    INSERT INTO demande (
        numero, qr_token, id_demandeur,
        id_type_visa, id_type_demande,
        id_statut, id_visa_transformable
    ) VALUES (
        'DEM-2026-NT-0001', v_qr, v_demandeur_id,
        v_type_visa_travailleur, v_type_demande_nouveau,
        v_statut_cree, v_vt_id
    ) RETURNING id INTO v_demande_id;

    INSERT INTO historique_statut_demande (id_demande, id_statut, date_changement) VALUES
        (v_demande_id, v_statut_cree, NOW() - INTERVAL '2 days');

    -- Pièces: on insère toutes les pièces possibles (communes + spécifiques visa), mais seulement 3 cochées
    INSERT INTO piece_fournie (id_demande, id_piece_justificative, is_present, date_upload)
    SELECT
        v_demande_id,
        pj.id,
        CASE WHEN pj.libelle IN (
            '02 Photos d''identité',
            'Notice de renseignement',
            'Certificat de résidence à Madagascar'
        ) THEN TRUE ELSE FALSE END,
        NOW() - INTERVAL '1 day'
    FROM piece_justificative pj
    WHERE pj.id_type_visa IS NULL OR pj.id_type_visa = v_type_visa_travailleur;

    -- =====================================================================
    -- DEMANDE #2 — SCAN TERMINÉ — toutes pièces cochées + 1 fichier photo par pièce
    -- =====================================================================
    INSERT INTO demandeur (
        nom, prenoms, date_naissance,
        id_situation_familiale, id_nationalite,
        profession, adresse_mada, contact_mada
    ) VALUES (
        'MARTIN', 'Sophie Claire', '1988-11-03',
        v_sit_mar, v_nat_fr,
        'CONSULTANTE', 'Ankorondrano, Antananarivo 101', '+261 33 44 555 66'
    ) RETURNING id INTO v_demandeur_id;

    INSERT INTO passeport (numero, id_demandeur, date_delivrance, date_expiration)
    VALUES ('FR-2020-987654', v_demandeur_id, '2020-09-18', '2030-09-17')
    RETURNING id INTO v_passeport_id;

    INSERT INTO visa_transformable (
        numero, id_passeport, id_demandeur,
        date_entree, lieu_entree, date_fin_visa
    ) VALUES (
        'VT-2026-NT-0002', v_passeport_id, v_demandeur_id,
        '2026-01-05', 'IVATO', '2026-04-04'
    ) RETURNING id INTO v_vt_id;

    v_qr := (
        substr(md5(random()::text || clock_timestamp()::text), 1, 8) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 9, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 13, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 17, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 21, 12)
    )::uuid;

    INSERT INTO demande (
        numero, qr_token, id_demandeur,
        id_type_visa, id_type_demande,
        id_statut, id_visa_transformable
    ) VALUES (
        'DEM-2026-NT-0002', v_qr, v_demandeur_id,
        v_type_visa_investisseur, v_type_demande_nouveau,
        v_statut_scan_termine, v_vt_id
    ) RETURNING id INTO v_demande_id;

    INSERT INTO historique_statut_demande (id_demande, id_statut, date_changement) VALUES
        (v_demande_id, v_statut_cree, NOW() - INTERVAL '20 days'),
        (v_demande_id, v_statut_scan_termine, NOW() - INTERVAL '1 day');

    INSERT INTO piece_fournie (id_demande, id_piece_justificative, is_present, date_upload)
    SELECT v_demande_id, pj.id, TRUE, NOW() - INTERVAL '3 days'
    FROM piece_justificative pj
    WHERE pj.id_type_visa IS NULL OR pj.id_type_visa = v_type_visa_investisseur;

    INSERT INTO piece_fichier (id_piece_fournie, file_path, nom_original, date_upload)
    SELECT
        pf.id,
        'seed_dem-2026-nt-0002_piece_' || pf.id || '.jpg',
        'piece.jpg',
        NOW() - INTERVAL '2 days'
    FROM piece_fournie pf
    WHERE pf.id_demande = v_demande_id AND pf.is_present = TRUE;

    -- =====================================================================
    -- DEMANDE #3 — VISA APPROUVE — toutes pièces cochées + 1 fichier photo par pièce
    -- =====================================================================
    INSERT INTO demandeur (
        nom, prenoms, date_naissance,
        id_situation_familiale, id_nationalite,
        profession, adresse_mada, contact_mada
    ) VALUES (
        'DUPONT', 'Hugo', '1990-02-14',
        v_sit_cel, v_nat_fr,
        'TECHNICIEN', 'Ampefiloha, Antananarivo 101', '+261 32 77 888 99'
    ) RETURNING id INTO v_demandeur_id;

    INSERT INTO passeport (numero, id_demandeur, date_delivrance, date_expiration)
    VALUES ('FR-2018-555666', v_demandeur_id, '2018-04-12', '2028-04-11')
    RETURNING id INTO v_passeport_id;

    INSERT INTO visa_transformable (
        numero, id_passeport, id_demandeur,
        date_entree, lieu_entree, date_fin_visa
    ) VALUES (
        'VT-2026-NT-0003', v_passeport_id, v_demandeur_id,
        '2026-03-01', 'IVATO', '2026-05-30'
    ) RETURNING id INTO v_vt_id;

    v_qr := (
        substr(md5(random()::text || clock_timestamp()::text), 1, 8) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 9, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 13, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 17, 4) || '-' ||
        substr(md5(random()::text || clock_timestamp()::text), 21, 12)
    )::uuid;

    INSERT INTO demande (
        numero, qr_token, id_demandeur,
        id_type_visa, id_type_demande,
        id_statut, id_visa_transformable
    ) VALUES (
        'DEM-2026-NT-0003', v_qr, v_demandeur_id,
        v_type_visa_travailleur, v_type_demande_nouveau,
        v_statut_visa_approuve, v_vt_id
    ) RETURNING id INTO v_demande_id;

    INSERT INTO historique_statut_demande (id_demande, id_statut, date_changement) VALUES
        (v_demande_id, v_statut_cree, NOW() - INTERVAL '15 days'),
        (v_demande_id, v_statut_visa_approuve, NOW() - INTERVAL '1 day');

    INSERT INTO piece_fournie (id_demande, id_piece_justificative, is_present, date_upload)
    SELECT v_demande_id, pj.id, TRUE, NOW() - INTERVAL '5 days'
    FROM piece_justificative pj
    WHERE pj.id_type_visa IS NULL OR pj.id_type_visa = v_type_visa_travailleur;

    INSERT INTO piece_fichier (id_piece_fournie, file_path, nom_original, date_upload)
    SELECT
        pf.id,
        'seed_dem-2026-nt-0003_piece_' || pf.id || '.jpg',
        'piece.jpg',
        NOW() - INTERVAL '4 days'
    FROM piece_fournie pf
    WHERE pf.id_demande = v_demande_id AND pf.is_present = TRUE;

END $$;