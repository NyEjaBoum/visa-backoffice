package com.visa.visa_backoffice.dto;

public class IndividuAutocompleteDTO {
    private Long individuId;
    private String nom;
    private String prenom;
    private String nomJeuneFille;
    private String dateNaissance;
    private String situationFamiliale;
    private String nationalite;
    private String profession;
    private String adresseMada;
    private String contact;
    private String passeportNumero;
    private String dateDelivrance;
    private String dateExpiration;

    // Getters et setters
    public Long getIndividuId() { return individuId; }
    public void setIndividuId(Long individuId) { this.individuId = individuId; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = nomJeuneFille; }
    public String getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
    public String getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(String situationFamiliale) { this.situationFamiliale = situationFamiliale; }
    public String getNationalite() { return nationalite; }
    public void setNationalite(String nationalite) { this.nationalite = nationalite; }
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getAdresseMada() { return adresseMada; }
    public void setAdresseMada(String adresseMada) { this.adresseMada = adresseMada; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getPasseportNumero() { return passeportNumero; }
    public void setPasseportNumero(String passeportNumero) { this.passeportNumero = passeportNumero; }
    public String getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(String dateDelivrance) { this.dateDelivrance = dateDelivrance; }
    public String getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(String dateExpiration) { this.dateExpiration = dateExpiration; }
}
