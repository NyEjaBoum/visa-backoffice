package com.visa.visa_backoffice.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

public class DemandeForm {

    // Individu

    private Integer demandeurId;

    private List<Integer> piecesFourniesIds;

    private String nom;
    
    private String prenoms;
    private String nomJeuneFille;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateNaissance;

    private Integer situationFamilialeId;
    private Integer nationaliteId;
    private String profession;
    
    private String adresseMada;
    
    private String contactMada;

    // Passeport
    private String numeroPasseport;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDelivrance;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateExpiration;

    // Demande
    private Integer typeVisaId;
    
    private Integer typeDemandeId;

    // Visa transformable (optionnel)
    private String visaTransformableNumero;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visaTransformableDateEntree;

    private String visaTransformableLieuEntree;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visaTransformableDateFinVisa;

    public void validateOrThrow() {
        requireNotBlank(nom, "Le nom est obligatoire");
        requireNotNull(dateNaissance, "La date de naissance est obligatoire");
        requireNotNull(nationaliteId, "La nationalité est obligatoire");
        requireNotBlank(adresseMada, "L'adresse à Madagascar est obligatoire");
        requireNotBlank(numeroPasseport, "Le numéro de passeport est obligatoire");
        requireNotNull(typeVisaId, "Le type de visa est obligatoire");
        requireNotNull(typeDemandeId, "Le type de demande est obligatoire");
    }

    private void requireNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    private void requireNotNull(Object value, String message) {
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Integer getDemandeurId() { return demandeurId; }
    public void setDemandeurId(Integer demandeurId) { this.demandeurId = demandeurId; }

    public List<Integer> getPiecesFourniesIds() { return piecesFourniesIds; }
    public void setPiecesFourniesIds(List<Integer> piecesFourniesIds) { this.piecesFourniesIds = piecesFourniesIds; }

    public String getPrenoms() { return prenoms; }
    public void setPrenoms(String prenoms) { this.prenoms = prenoms; }

    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = nomJeuneFille; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Integer getSituationFamilialeId() { return situationFamilialeId; }
    public void setSituationFamilialeId(Integer situationFamilialeId) { this.situationFamilialeId = situationFamilialeId; }

    public Integer getNationaliteId() { return nationaliteId; }
    public void setNationaliteId(Integer nationaliteId) { this.nationaliteId = nationaliteId; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }

    public String getAdresseMada() { return adresseMada; }
    public void setAdresseMada(String adresseMada) { this.adresseMada = adresseMada; }

    public String getContactMada() { return contactMada; }
    public void setContactMada(String contactMada) { this.contactMada = contactMada; }

    public String getNumeroPasseport() { return numeroPasseport; }
    public void setNumeroPasseport(String numeroPasseport) { this.numeroPasseport = numeroPasseport; }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    public Integer getTypeVisaId() { return typeVisaId; }
    public void setTypeVisaId(Integer typeVisaId) { this.typeVisaId = typeVisaId; }

    public Integer getTypeDemandeId() { return typeDemandeId; }
    public void setTypeDemandeId(Integer typeDemandeId) { this.typeDemandeId = typeDemandeId; }

    public String getVisaTransformableNumero() { return visaTransformableNumero; }
    public void setVisaTransformableNumero(String visaTransformableNumero) { this.visaTransformableNumero = visaTransformableNumero; }

    public LocalDate getVisaTransformableDateEntree() { return visaTransformableDateEntree; }
    public void setVisaTransformableDateEntree(LocalDate visaTransformableDateEntree) { this.visaTransformableDateEntree = visaTransformableDateEntree; }

    public String getVisaTransformableLieuEntree() { return visaTransformableLieuEntree; }
    public void setVisaTransformableLieuEntree(String visaTransformableLieuEntree) { this.visaTransformableLieuEntree = visaTransformableLieuEntree; }

    public LocalDate getVisaTransformableDateFinVisa() { return visaTransformableDateFinVisa; }
    public void setVisaTransformableDateFinVisa(LocalDate visaTransformableDateFinVisa) { this.visaTransformableDateFinVisa = visaTransformableDateFinVisa; }
}
