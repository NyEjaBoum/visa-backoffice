package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "type_demande_visa")
public class TypeDemande {

    @Id
    private Integer id;
    private String libelle;

    public Integer getId() { return id; }
    public String getLibelle() { return libelle; }
}
