--liquibase formatted sql

--changeset sprint4:view-suivi-demande labels:sprint4 runOnChange:true comment:Vue suivi public (demande + demandeur + passeport + historique)
DROP VIEW IF EXISTS v_suivi_demande;

CREATE VIEW v_suivi_demande AS
SELECT
    d.id AS demande_id,
    d.qr_token AS qr_token,
    d.numero AS demande_numero,
    d.date_creation AS demande_date_creation,

    de.id AS demandeur_id,
    de.nom AS demandeur_nom,
    de.prenoms AS demandeur_prenoms,

    p_last.numero AS passeport_numero,

    h.id AS historique_id,
    h.date_changement AS date_changement,
    s.libelle AS statut_libelle

FROM demande d
JOIN demandeur de ON de.id = d.id_demandeur

LEFT JOIN LATERAL (
    SELECT p2.numero
    FROM passeport p2
    WHERE p2.id_demandeur = de.id
    ORDER BY p2.id DESC
    LIMIT 1
) p_last ON true

JOIN historique_statut_demande h ON h.id_demande = d.id
JOIN statut s ON s.id = h.id_statut;

--changeset sprint4:idx-historique-demande-date labels:sprint4 comment:Index utile pour tri chronologique
CREATE INDEX IF NOT EXISTS idx_hist_demande_date ON historique_statut_demande(id_demande, date_changement);
