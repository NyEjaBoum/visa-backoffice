package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_situation_familiale")
    private SituationFamiliale situationFamiliale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nationalite")
    private Nationalite nationalite;

    private String profession;

    @Column(name = "adresse_mada")
    private String adresseMada;

    @Column(name = "contact_mada")
    private String contactMada;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public Integer getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) {
        this.nom = trim(nom);
        if (blank(this.nom)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom est obligatoire");
        }
    }

    public String getPrenoms() { return prenoms; }
    public void setPrenoms(String prenoms) { this.prenoms = trim(prenoms); }

    public String getNomJeuneFille() { return nomJeuneFille; }
    public void setNomJeuneFille(String nomJeuneFille) { this.nomJeuneFille = trim(nomJeuneFille); }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) {
        if (dateNaissance == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date de naissance est obligatoire");
        }
        this.dateNaissance = dateNaissance;
    }

    public SituationFamiliale getSituationFamiliale() { return situationFamiliale; }
    public void setSituationFamiliale(SituationFamiliale situationFamiliale) { this.situationFamiliale = situationFamiliale; }

    public Nationalite getNationalite() { return nationalite; }
    public void setNationalite(Nationalite nationalite) { this.nationalite = nationalite; }

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = trim(profession); }

    public String getAdresseMada() { return adresseMada; }
    public void setAdresseMada(String adresseMada) {
        this.adresseMada = trim(adresseMada);
        if (blank(this.adresseMada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'adresse à Madagascar est obligatoire");
        }
    }

    public String getContactMada() { return contactMada; }
    public void setContactMada(String contactMada) { this.contactMada = trim(contactMada); }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getUpdatedDate() { return updatedDate; }
    public void setUpdatedDate(LocalDateTime updatedDate) { this.updatedDate = updatedDate; }

    public void setId(Integer id) { this.id = id; }

    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String trim(String s) { return s == null ? null : s.trim(); }

}
