package com.visa.visa_backoffice.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

public class DemandeAntecedentForm {

    // Cas normal : individu déjà en base (sélectionné via autocomplete)
    private Integer demandeurId;

    // Cas rattrapage : saisie complète de l'individu
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

    // Cas rattrapage : passeport
    private String numeroPasseport;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateDelivrance;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateExpiration;

    // Visa transformable — preuve d'origine (obligatoire dans les deux cas)
    private String visaTransformableNumero;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visaTransformableDateEntree;

    private String visaTransformableLieuEntree;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate visaTransformableDateFinVisa;

    // Type de demande B : DUPLICATA (2) ou TRANSFERT (3)
    private Integer typeDemandeId;

    // Type de visa
    private Integer typeVisaId;

    public void validateCasNormal() {
        requireNotNull(demandeurId, "Demandeur obligatoire — utilisez la recherche pour sélectionner un dossier existant");
        requireNotNull(typeDemandeId, "Le type de demande est obligatoire");
        requireNotNull(typeVisaId, "Le type de visa est obligatoire");
        requireNotBlank(visaTransformableNumero, "Le numéro du visa transformable est obligatoire");
    }

    public void validateRattrapage() {
        requireNotBlank(nom, "Le nom est obligatoire");
        requireNotNull(dateNaissance, "La date de naissance est obligatoire");
        requireNotNull(nationaliteId, "La nationalité est obligatoire");
        requireNotBlank(adresseMada, "L'adresse à Madagascar est obligatoire");
        requireNotBlank(numeroPasseport, "Le numéro de passeport est obligatoire");
        requireNotNull(typeDemandeId, "Le type de demande est obligatoire");
        requireNotNull(typeVisaId, "Le type de visa est obligatoire");
        requireNotBlank(visaTransformableNumero, "Le numéro du visa transformable est obligatoire");
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

    public Integer getDemandeurId() { return demandeurId; }
    public void setDemandeurId(Integer demandeurId) { this.demandeurId = demandeurId; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

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

    public String getVisaTransformableNumero() { return visaTransformableNumero; }
    public void setVisaTransformableNumero(String ref) { this.visaTransformableNumero = ref; }

    public LocalDate getVisaTransformableDateEntree() { return visaTransformableDateEntree; }
    public void setVisaTransformableDateEntree(LocalDate d) { this.visaTransformableDateEntree = d; }

    public String getVisaTransformableLieuEntree() { return visaTransformableLieuEntree; }
    public void setVisaTransformableLieuEntree(String lieu) { this.visaTransformableLieuEntree = lieu; }

    public LocalDate getVisaTransformableDateFinVisa() { return visaTransformableDateFinVisa; }
    public void setVisaTransformableDateFinVisa(LocalDate d) { this.visaTransformableDateFinVisa = d; }

    public Integer getTypeDemandeId() { return typeDemandeId; }
    public void setTypeDemandeId(Integer typeDemandeId) { this.typeDemandeId = typeDemandeId; }

    public Integer getTypeVisaId() { return typeVisaId; }
    public void setTypeVisaId(Integer typeVisaId) { this.typeVisaId = typeVisaId; }
}
