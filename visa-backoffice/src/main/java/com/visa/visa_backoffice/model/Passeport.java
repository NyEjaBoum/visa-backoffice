package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Entity
@Table(name = "passeport")
public class Passeport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero")
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur")
    private Demandeur demandeur;

    @Column(name = "date_delivrance")
    private LocalDate dateDelivrance;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) {
        this.numero = trim(numero);
        if (this.numero == null || this.numero.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numéro de passeport est obligatoire");
        }
    }

    public LocalDate getDateDelivrance() { return dateDelivrance; }
    public void setDateDelivrance(LocalDate dateDelivrance) { this.dateDelivrance = dateDelivrance; }

    public LocalDate getDateExpiration() { return dateExpiration; }
    public void setDateExpiration(LocalDate dateExpiration) { this.dateExpiration = dateExpiration; }

    private String trim(String s) { return s == null ? null : s.trim(); }
}
