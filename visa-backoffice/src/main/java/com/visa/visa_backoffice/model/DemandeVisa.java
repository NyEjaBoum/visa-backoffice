package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Entity
@Table(name = "demande_visa")
public class DemandeVisa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero", unique = true, nullable = false)
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

    @Column(name = "qr_code_token", unique = true)
    private String qrCodeToken;

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

    public Individu getIndividu() { return individu; }
    public void setIndividu(Individu individu) {
        if (individu == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Individu obligatoire");
        }
        this.individu = individu;
    }

    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) {
        if (passeport == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Passeport obligatoire");
        }
        this.passeport = passeport;
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

    public String getQrCodeToken() { return qrCodeToken; }
    public void setQrCodeToken(String qrCodeToken) { this.qrCodeToken = qrCodeToken; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
