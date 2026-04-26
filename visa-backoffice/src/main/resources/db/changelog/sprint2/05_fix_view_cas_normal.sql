--changeset sprint2:view-dossier-complet-final-unique labels:sprint2 runOnChange:true
DROP VIEW IF EXISTS v_dossier_complet;

CREATE VIEW v_dossier_complet AS
SELECT DISTINCT ON (p.numero)
    -- 1. INFOS DEMANDE
    d.id AS demande_id,
    d.numero AS demande_numero,
    d.date_creation AS demande_date_creation,
    s.libelle AS statut_actuel,
    td.id AS type_demande_id,
    td.libelle AS type_demande_libelle,
    tv.id AS type_visa_id,
    tv.libelle AS type_visa_libelle,

    -- 2. ÉTAT CIVIL DEMANDEUR
    de.id AS demandeur_id,
    de.nom,
    de.prenoms,
    de.nom_jeune_fille,
    de.date_naissance,
    de.profession,
    de.adresse_mada,
    de.contact_mada,
    sf.id AS situation_familiale_id,
    sf.libelle AS situation_familiale,
    n.id AS nationalite_id,
    n.libelle AS nationalite,

    -- 3. INFOS PASSEPORT
    p.id AS passeport_id,
    p.numero AS passeport_numero,
    p.date_delivrance AS passeport_delivrance,
    p.date_expiration AS passeport_expiration,

    -- 4. VISA TRANSFORMABLE
    vt.id AS visa_transformable_id,
    vt.numero AS visa_transformable_numero,
    vt.date_entree AS vt_date_entree,
    vt.lieu_entree AS vt_lieu_entree,
    vt.date_fin_visa AS vt_date_fin,

    -- 5. CARTE RÉSIDENT
    cr.id AS carte_resident_id,
    cr.numero AS carte_resident_numero,
    cr.date_debut AS cr_date_debut,
    cr.date_fin AS cr_date_fin,

    -- 6. PIÈCES DÉJÀ FOURNIES
    (SELECT array_agg(pf.id_piece_justificative) 
     FROM piece_fournie pf 
     WHERE pf.id_demande = d.id) AS pieces_fournies_ids

FROM passeport p
JOIN demandeur de ON p.id_demandeur = de.id
LEFT JOIN visa_transformable vt ON vt.id_passeport = p.id
-- On lie à la demande via le demandeur et le visa (ou passeport si ta table évolue)
LEFT JOIN demande d ON d.id_demandeur = de.id 
    AND (vt.id IS NULL OR d.id_visa_transformable = vt.id)
LEFT JOIN statut s ON d.id_statut = s.id
LEFT JOIN type_demande td ON d.id_type_demande = td.id
LEFT JOIN type_visa tv ON d.id_type_visa = tv.id
LEFT JOIN situation_familiale sf ON de.id_situation_familiale = sf.id
LEFT JOIN nationalite n ON de.id_nationalite = n.id
LEFT JOIN carte_resident cr ON cr.id_passeport = p.id

-- Tri par numéro de passeport (pour le DISTINCT ON)
-- puis par date de création décroissante ; NULLS LAST = passeports sans demande en dernier
ORDER BY p.numero, d.date_creation DESC NULLS LAST;