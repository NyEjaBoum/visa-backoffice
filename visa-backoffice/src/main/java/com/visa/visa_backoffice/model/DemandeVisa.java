package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_visa")
public class DemandeVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "num_demande")
    private String numDemande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu")
    private Individu individu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pass")
    private Passeport passeport;

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

    public String getNumDemande() { return numDemande; }
    public void setNumDemande(String numDemande) { this.numDemande = numDemande; }

    public Individu getIndividu() { return individu; }
    public void setIndividu(Individu individu) { this.individu = individu; }

    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) { this.passeport = passeport; }

    public TypeVisa getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeVisa typeVisa) { this.typeVisa = typeVisa; }

    public TypeDemande getTypeDemande() { return typeDemande; }
    public void setTypeDemande(TypeDemande typeDemande) { this.typeDemande = typeDemande; }

    public Statut getStatut() { return statut; }
    public void setStatut(Statut statut) { this.statut = statut; }

    public VisaTransformable getVisaTransformable() { return visaTransformable; }
    public void setVisaTransformable(VisaTransformable visaTransformable) { this.visaTransformable = visaTransformable; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
