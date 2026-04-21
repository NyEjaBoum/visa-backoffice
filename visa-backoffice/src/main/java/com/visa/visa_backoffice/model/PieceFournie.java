package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "piece_fournie")
public class PieceFournie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande")
    private DemandeVisa demande;

    @Column(name = "libelle_piece")
    private String libellePiece;

    @Column(name = "is_present")
    private Boolean present;

    public Integer getId() {
        return id;
    }

    public DemandeVisa getDemande() {
        return demande;
    }

    public void setDemande(DemandeVisa demande) {
        this.demande = demande;
    }

    public String getLibellePiece() {
        return libellePiece;
    }

    public void setLibellePiece(String libellePiece) {
        this.libellePiece = libellePiece;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }
}
