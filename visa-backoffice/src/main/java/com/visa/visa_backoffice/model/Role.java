package com.visa.visa_backoffice.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @Id
    private Integer id;

    @Column(name = "nom_role")
    private String nomRole;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER)
    private List<RoleAction> actions;

    public Integer getId() { return id; }
    public String getNomRole() { return nomRole; }
    public List<RoleAction> getActions() { return actions; }
}
