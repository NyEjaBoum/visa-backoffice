package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "v_dossier_complet")
@Getter
public class DossierComplet {

    @Id
    @Column(name = "demande_id")
    private Integer demandeId;

    @Column(name = "demande_numero")
    private String demandeNumero;

    @Column(name = "demande_date_creation")
    private LocalDateTime demandeDateCreation;

    @Column(name = "statut_actuel")
    private String statutActuel;

    @Column(name = "type_demande_libelle")
    private String typeDemandeLibelle;

    @Column(name = "type_visa_libelle")
    private String typeVisaLibelle;

    // --- DEMANDEUR ---
    @Column(name = "demandeur_id")
    private Integer demandeurId;

    private String nom;
    private String prenoms;

    @Column(name = "nom_jeune_fille")
    private String nomJeuneFille;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String profession;

    @Column(name = "adresse_mada")
    private String adresseMada;

    @Column(name = "contact_mada")
    private String contactMada;

    @Column(name = "situation_familiale")
    private String situationFamiliale;

    private String nationalite;

    // --- PASSEPORT ---
    @Column(name = "passeport_id")
    private Integer passeportId;

    @Column(name = "passeport_numero")
    private String passeportNumero;

    @Column(name = "passeport_delivrance")
    private LocalDate passeportDelivrance;

    @Column(name = "passeport_expiration")
    private LocalDate passeportExpiration;

    // --- VISA TRANSFORMABLE ---
    @Column(name = "visa_transformable_id")
    private Integer visaTransformableId;

    @Column(name = "visa_transformable_numero")
    private String visaTransformableNumero;

    @Column(name = "vt_date_entree")
    private LocalDate vtDateEntree;

    @Column(name = "vt_lieu_entree")
    private String vtLieuEntree;

    @Column(name = "vt_date_fin")
    private LocalDate vtDateFin;
}
