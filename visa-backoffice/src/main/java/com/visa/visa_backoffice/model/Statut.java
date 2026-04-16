package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "statut")
public class Statut {

    @Id
    private Integer id;
    private String libelle;

    public Integer getId() { return id; }
    public String getLibelle() { return libelle; }
}
