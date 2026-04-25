package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande")
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero", unique = true, nullable = false)
    private String numDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur")
    private Demandeur demandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_visa")
    private TypeVisa typeVisa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_demande")
    private TypeDemande typeDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_statut")
    private Statut statut;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visa_transformable")
    private VisaTransformable visaTransformable;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumDemande() { return numDemande; }
    public void setNumDemande(String numDemande) {
        this.numDemande = trim(numDemande);
        if (blank(this.numDemande)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numéro de demande est obligatoire");
        }
    }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) {
        if (demandeur == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Demandeur obligatoire");
        }
        this.demandeur = demandeur;
    }

    public TypeVisa getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeVisa typeVisa) {
        if (typeVisa == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de visa est obligatoire");
        }
        this.typeVisa = typeVisa;
    }

    public TypeDemande getTypeDemande() { return typeDemande; }
    public void setTypeDemande(TypeDemande typeDemande) {
        if (typeDemande == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de demande est obligatoire");
        }
        this.typeDemande = typeDemande;
    }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public VisaTransformable getVisaTransformable() { return visaTransformable; }
    public void setVisaTransformable(VisaTransformable visaTransformable) { this.visaTransformable = visaTransformable; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
