package com.jlumanog_dev.patchlens_spring_backend.dto;

import jakarta.persistence.Column;

import java.math.BigInteger;

public class UserDTO {
    private String personaName;
    private String email;
    private String role;
    private BigInteger playerIdField;
    @Override
    public String toString(){
        return "persona: " + this.getPersonaName() + "\nplayerIdField: " + this.getPlayerIdField() + "\nrole: " + this.getRole();
    }
    public BigInteger getPlayerIdField() {
        return playerIdField;
    }

    public void setPlayerIdField(BigInteger playerIdField) {
        this.playerIdField = playerIdField;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String personaName) {
        this.personaName = personaName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
