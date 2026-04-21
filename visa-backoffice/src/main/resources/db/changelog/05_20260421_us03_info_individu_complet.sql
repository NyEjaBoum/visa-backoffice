SET search_path TO dev;

CREATE OR REPLACE VIEW v_individu_autocomplete AS
SELECT
    i.id                        AS individu_id,
    i.nom,
    i.prenoms                   AS prenom,
    i.nom_jeune_fille,
    i.date_naissance,
    sf.libelle                   AS situation_familiale,
    i.nationalite,
    i.profession,
    i.adresse_mada,
    i.contact_mada              AS contact,
    p.numero_pass               AS passeport_numero,
    p.date_delivrance,
    p.date_expiration
FROM individu i
LEFT JOIN situation_familiale sf ON sf.id = i.id_situation_familiale
LEFT JOIN passeport p ON p.id_individu = i.id AND p.is_active = TRUE;
