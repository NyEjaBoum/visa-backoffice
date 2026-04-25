package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "nationalite")
public class Nationalite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String libelle;

    public Integer getId() { return id; }
    public String getLibelle() { return libelle; }
    public void setId(Integer id) { this.id = id; }
    public void setLibelle(String libelle) {
        this.libelle = trim(libelle);
        if (blank(this.libelle)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Libellé nationalité obligatoire");
        }
    }

    private boolean blank(String s) { return s == null || s.isBlank(); }
    private String trim(String s) { return s == null ? null : s.trim(); }
}
