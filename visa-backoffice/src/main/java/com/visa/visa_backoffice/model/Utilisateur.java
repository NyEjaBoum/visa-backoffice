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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_role")
    private Role role;

    public Integer getId() { return id; }
    public String getIdentifiant() { return identifiant; }
    public String getMdp() { return mdp; }
    public Role getRole() { return role; }
}
