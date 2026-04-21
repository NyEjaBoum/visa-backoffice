package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "situation_familiale")
public class SituationFamiliale {

    @Id
    private Integer id;

    private String libelle;

    public Integer getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }
}
