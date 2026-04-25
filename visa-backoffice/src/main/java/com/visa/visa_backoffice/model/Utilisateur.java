package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String identifiant;
    private String mdp;
    private String role;

    public Integer getId() { return id; }
    public String getIdentifiant() { return identifiant; }
    public String getMdp() { return mdp; }
    public String getRole() { return role; }

    public void setId(Integer id) { this.id = id; }
    public void setIdentifiant(String identifiant) { this.identifiant = identifiant; }
    public void setMdp(String mdp) { this.mdp = mdp; }
    public void setRole(String role) { this.role = role; }
}
