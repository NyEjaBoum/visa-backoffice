package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "piece_fournie")
public class PieceFournie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demande")
    private Demande demande;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_piece_justificative")
    private PieceJustificative pieceJustificative;

    @Column(name = "is_present")
    private Boolean isPresent;

    @Column(name = "date_upload")
    private LocalDateTime dateUpload;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Demande getDemande() { return demande; }
    public void setDemande(Demande demande) { this.demande = demande; }

    public PieceJustificative getPieceJustificative() { return pieceJustificative; }
    public void setPieceJustificative(PieceJustificative pieceJustificative) { this.pieceJustificative = pieceJustificative; }

    public Boolean getIsPresent() { return isPresent; }
    public void setIsPresent(Boolean present) { isPresent = present; }

    public LocalDateTime getDateUpload() { return dateUpload; }
    public void setDateUpload(LocalDateTime dateUpload) { this.dateUpload = dateUpload; }
}
