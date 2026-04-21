package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "visa_transformable")
public class VisaTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_individu")
    private Individu individu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passeport")
    private Passeport passeport;

    @Column(name = "numero_reference", unique = true)
    private String numeroReference;

    @Column(name = "date_entree")
    private LocalDate dateEntree;

    @Column(name = "lieu_entree")
    private String lieuEntree;

    @Column(name = "date_fin_visa")
    private LocalDate dateFinVisa;

    public Integer getId() { return id; }

    public Individu getIndividu() { return individu; }
    public void setIndividu(Individu individu) { this.individu = individu; }

    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) { this.passeport = passeport; }

    public String getNumeroReference() { return numeroReference; }
    public void setNumeroReference(String numeroReference) { this.numeroReference = numeroReference; }

    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }

    public String getLieuEntree() { return lieuEntree; }
    public void setLieuEntree(String lieuEntree) { this.lieuEntree = lieuEntree; }

    public LocalDate getDateFinVisa() { return dateFinVisa; }
    public void setDateFinVisa(LocalDate dateFinVisa) { this.dateFinVisa = dateFinVisa; }
}
