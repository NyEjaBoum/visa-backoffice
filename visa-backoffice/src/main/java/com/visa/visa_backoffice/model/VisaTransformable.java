package com.visa.visa_backoffice.model;

import com.visa.visa_backoffice.dto.DemandeForm;
import jakarta.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Entity
@Table(name = "visa_transformable")
public class VisaTransformable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "numero", nullable = false, unique = true)
    private String numero;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_passeport")
    private Passeport passeport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_demandeur")
    private Demandeur demandeur;

    @Column(name = "date_entree")
    private LocalDate dateEntree;

    @Column(name = "lieu_entree")
    private String lieuEntree;

    @Column(name = "date_fin_visa")
    private LocalDate dateFinVisa;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) {
        this.numero = numero == null ? null : numero.trim();
        if (this.numero == null || this.numero.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numéro du visa transformable est obligatoire");
        }
    }

    public Passeport getPasseport() { return passeport; }
    public void setPasseport(Passeport passeport) { this.passeport = passeport; }

    public Demandeur getDemandeur() { return demandeur; }
    public void setDemandeur(Demandeur demandeur) { this.demandeur = demandeur; }

    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }

    public String getLieuEntree() { return lieuEntree; }
    public void setLieuEntree(String lieuEntree) { this.lieuEntree = lieuEntree == null ? null : lieuEntree.trim(); }

    public LocalDate getDateFinVisa() { return dateFinVisa; }
    public void setDateFinVisa(LocalDate dateFinVisa) { this.dateFinVisa = dateFinVisa; }

    public void updateFromForm(DemandeForm form, Passeport passeport, Demandeur demandeur) {
        setNumero(form.getVisaTransformableNumero());
        setPasseport(passeport);
        setDemandeur(demandeur);
        setDateEntree(form.getVisaTransformableDateEntree());
        setLieuEntree(form.getVisaTransformableLieuEntree());
        setDateFinVisa(form.getVisaTransformableDateFinVisa());
    }
}
