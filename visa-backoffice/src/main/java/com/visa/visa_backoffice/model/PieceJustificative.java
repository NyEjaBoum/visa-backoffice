package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "piece_justificative")
public class PieceJustificative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_visa")
    private TypeVisa typeVisa;

    @Column(name = "est_obligatoire")
    private Boolean estObligatoire;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) {
        this.libelle = trim(libelle);
        if (blank(this.libelle)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Libellé pièce justificative obligatoire");
        }
    }

    public TypeVisa getTypeVisa() { return typeVisa; }
    public void setTypeVisa(TypeVisa typeVisa) { this.typeVisa = typeVisa; }

    public Boolean getEstObligatoire() { return estObligatoire; }
    public void setEstObligatoire(Boolean estObligatoire) { this.estObligatoire = estObligatoire; }

    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
