package com.visa.visa_backoffice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role_action")
public class RoleAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_code")
    private String roleCode;

    private String action;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}
