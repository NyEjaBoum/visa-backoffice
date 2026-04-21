package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "piece_type")
public class PieceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_type_visa")
    private TypeVisa typeVisa;

    public Integer getId() { return id; }
    public String getLibelle() { return libelle; }
    public TypeVisa getTypeVisa() { return typeVisa; }
}
