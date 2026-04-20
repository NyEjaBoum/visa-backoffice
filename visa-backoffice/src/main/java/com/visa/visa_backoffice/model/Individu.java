package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "individu")
public class Individu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private String prenoms;

    @Column(name = "nom_jeune_fille")
    private String nomJeuneFille;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    @Column(name = "situation_familiale")
    private String situationFamiliale;

    private String nationalite;
    private String profession;

    @Column(name = "adresse_mada")
    private String adresseMada;

    @Column(name = "contact_mada")
    private String contactMada;

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenoms() {
        return prenoms;
    }

    public void setPrenoms(String prenoms) {
        this.prenoms = prenoms;
    }

    public String getNomJeuneFille() {
        return nomJeuneFille;
    }

    public void setNomJeuneFille(String nomJeuneFille) {
        this.nomJeuneFille = nomJeuneFille;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getSituationFamiliale() {
        return situationFamiliale;
    }

    public void setSituationFamiliale(String situationFamiliale) {
        this.situationFamiliale = situationFamiliale;
    }

    public String getNationalite() {
        return nationalite;
    }

    public void setNationalite(String nationalite) {
        this.nationalite = nationalite;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAdresseMada() {
        return adresseMada;
    }

    public void setAdresseMada(String adresseMada) {
        this.adresseMada = adresseMada;
    }

    public String getContactMada() {
        return contactMada;
    }

    public void setContactMada(String contactMada) {
        this.contactMada = contactMada;
    }
}
