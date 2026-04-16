package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_action")
public class RoleAction {

    @Id
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_role")
    private Role role;

    @Column(name = "nom_action")
    private String nomAction;

    public Integer getId() { return id; }
    public String getNomAction() { return nomAction; }
}
