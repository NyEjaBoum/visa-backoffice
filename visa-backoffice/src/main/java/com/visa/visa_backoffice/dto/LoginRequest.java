package com.visa.visa_backoffice.dto;

public class LoginRequest {
    private String identifiant;
    private String mdp;

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    // Aliases pour le template HTML
    public String getUsername() {
        return identifiant;
    }

    public void setUsername(String username) {
        this.identifiant = username;
    }

    public String getPassword() {
        return mdp;
    }

    public void setPassword(String password) {
        this.mdp = password;
    }
}
