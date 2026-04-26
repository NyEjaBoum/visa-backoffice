--changeset sprint2:view-dossier-complet-final labels:sprint2 runOnChange:true
DROP VIEW IF EXISTS v_dossier_complet;

CREATE VIEW v_dossier_complet AS
SELECT DISTINCT ON (de.id)
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

    -- 3. INFOS PASSEPORT (Le plus récent)
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

    -- 5. CARTE RÉSIDENT (Nouvelle section)
    cr.id AS carte_resident_id,
    cr.numero AS carte_resident_numero,
    cr.date_debut AS cr_date_debut,
    cr.date_fin AS cr_date_fin,

    -- 6. PIÈCES DÉJÀ FOURNIES
    (SELECT array_agg(pf.id_piece_justificative) 
     FROM piece_fournie pf 
     WHERE pf.id_demande = d.id) AS pieces_fournies_ids

FROM demandeur de
-- On part du demandeur pour garantir le DISTINCT ON (de.id)
LEFT JOIN demande d ON d.id_demandeur = de.id
LEFT JOIN situation_familiale sf ON de.id_situation_familiale = sf.id
LEFT JOIN nationalite n ON de.id_nationalite = n.id
LEFT JOIN passeport p ON p.id_demandeur = de.id
LEFT JOIN type_visa tv ON d.id_type_visa = tv.id
LEFT JOIN type_demande td ON d.id_type_demande = td.id
LEFT JOIN statut s ON d.id_statut = s.id
LEFT JOIN visa_transformable vt ON d.id_visa_transformable = vt.id
LEFT JOIN carte_resident cr ON cr.id_demande = d.id

-- Tri par demandeur puis par date de demande (la plus récente d'abord)
-- et enfin par date d'expiration de passeport pour avoir le dernier valide
ORDER BY de.id, d.date_creation DESC, p.date_expiration DESC;


--changeset sprint2:indexes-performance-autocomplete labels:sprint2
-- 1. Index pour la recherche par NOM et PRENOM (Autocomplete classique)
CREATE INDEX IF NOT EXISTS idx_demandeur_nom_prenom_trgm ON demandeur (nom, prenoms);

-- 2. Index pour la recherche par NUMÉRO (Le coeur de ton système)
-- Ces index permettent au "findByNumero" dans ton Java d'être instantané
CREATE INDEX IF NOT EXISTS idx_passeport_numero ON passeport(numero);
CREATE INDEX IF NOT EXISTS idx_carte_resident_numero ON carte_resident(numero);
CREATE INDEX IF NOT EXISTS idx_visa_trans_numero ON visa_transformable(numero);

-- 3. Index pour optimiser les JOINTURES de la vue
-- Sans ça, le "LEFT JOIN" ralentit quand tu as beaucoup de données
CREATE INDEX IF NOT EXISTS idx_demande_id_demandeur ON demande(id_demandeur);
CREATE INDEX IF NOT EXISTS idx_passeport_id_demandeur ON passeport(id_demandeur);
CREATE INDEX IF NOT EXISTS idx_carte_resident_id_demande ON carte_resident(id_demande);

-- 4. Index pour le tri (ORDER BY dans la vue)
-- Accélère le "DISTINCT ON" et le "ORDER BY date_creation DESC"
CREATE INDEX IF NOT EXISTS idx_demande_tri_recent ON demande(id_demandeur, date_creation DESC);