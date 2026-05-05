package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Immutable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Immutable
@Table(name = "v_dossier_complet")
public class DossierComplet {

    @Column(name = "demande_id")
    private Integer demandeId;

    @Column(name = "demande_numero")
    private String demandeNumero;

    @Column(name = "demande_date_creation")
    private LocalDateTime demandeDateCreation;

    @Column(name = "statut_actuel")
    private String statutActuel;

    @Column(name = "type_demande_id")
    private Integer typeDemandeId;

    @Column(name = "type_demande_libelle")
    private String typeDemandeLibelle;

    @Column(name = "type_visa_id")
    private Integer typeVisaId;

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

    @Column(name = "situation_familiale_id")
    private Integer situationFamilialeId;

    @Column(name = "situation_familiale")
    private String situationFamiliale;

    @Column(name = "nationalite_id")
    private Integer nationaliteId;

    private String nationalite;

    // --- PASSEPORT ---
    @Id
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

    @Column(name = "carte_resident_numero")
    private String carteResidentNumero;


    @Column(name = "vt_date_entree")
    private LocalDate vtDateEntree;

    @Column(name = "vt_lieu_entree")
    private String vtLieuEntree;

    @Column(name = "vt_date_fin")
    private LocalDate vtDateFin;

    public Integer getDemandeId() {
        return demandeId;
    }

    public String getDemandeNumero() {
        return demandeNumero;
    }

    public LocalDateTime getDemandeDateCreation() {
        return demandeDateCreation;
    }

    public String getStatutActuel() {
        return statutActuel;
    }

    public Integer getTypeDemandeId() {
        return typeDemandeId;
    }

    public String getTypeDemandeLibelle() {
        return typeDemandeLibelle;
    }

    public Integer getTypeVisaId() {
        return typeVisaId;
    }

    public String getTypeVisaLibelle() {
        return typeVisaLibelle;
    }

    public Integer getDemandeurId() {
        return demandeurId;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public String getNomJeuneFille() {
        return nomJeuneFille;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public String getProfession() {
        return profession;
    }

    public String getAdresseMada() {
        return adresseMada;
    }

    public String getContactMada() {
        return contactMada;
    }

    public Integer getSituationFamilialeId() {
        return situationFamilialeId;
    }

    public String getSituationFamiliale() {
        return situationFamiliale;
    }

    public Integer getNationaliteId() {
        return nationaliteId;
    }

    public String getNationalite() {
        return nationalite;
    }

    public Integer getPasseportId() {
        return passeportId;
    }

    public String getPasseportNumero() {
        return passeportNumero;
    }

    public LocalDate getPasseportDelivrance() {
        return passeportDelivrance;
    }

    public LocalDate getPasseportExpiration() {
        return passeportExpiration;
    }

    public Integer getVisaTransformableId() {
        return visaTransformableId;
    }

    public String getVisaTransformableNumero() {
        return visaTransformableNumero;
    }

    public String getCarteResidentNumero() {
        return carteResidentNumero;
    }

    public LocalDate getVtDateEntree() {
        return vtDateEntree;
    }

    public String getVtLieuEntree() {
        return vtLieuEntree;
    }

    public LocalDate getVtDateFin() {
        return vtDateFin;
    }
}
