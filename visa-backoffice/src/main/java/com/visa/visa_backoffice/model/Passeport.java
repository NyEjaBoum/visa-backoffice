package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "passeport")
public class Passeport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu")
    private Individu individu;

    @Column(name = "numero_pass")
    private String numeroPass;

    @Column(name = "date_delivrance")
    private LocalDate dateDelivrance;

    @Column(name = "date_expiration")
    private LocalDate dateExpiration;

    @Column(name = "is_active")
    private Boolean active;

    public Integer getId() {
        return id;
    }

    public Individu getIndividu() {
        return individu;
    }

    public void setIndividu(Individu individu) {
        this.individu = individu;
    }

    public String getNumeroPass() {
        return numeroPass;
    }

    public void setNumeroPass(String numeroPass) {
        this.numeroPass = numeroPass;
    }

    public LocalDate getDateDelivrance() {
        return dateDelivrance;
    }

    public void setDateDelivrance(LocalDate dateDelivrance) {
        this.dateDelivrance = dateDelivrance;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
