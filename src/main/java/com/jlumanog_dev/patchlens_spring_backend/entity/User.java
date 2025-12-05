package com.jlumanog_dev.patchlens_spring_backend.entity;

import jakarta.persistence.*;

import java.math.BigInteger;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name="username")
    private String personaName;
    @Column(name = "email")
    private String email;
    @Column(name = "pin_hash")
    private String pinField;
    @Column(name="sha_lookup")
    private String shaLookup;
    @Column(name = "role")
    private String role;
    @Column(name="player_id")
    private BigInteger playerIdField;


    public String getShaLookup() {
        return shaLookup;
    }

    public void setShaLookup(String shaLookup) {
        this.shaLookup = shaLookup;
    }

    public BigInteger getPlayerField() {
        return playerIdField;
    }

    public void setPlayerIdField(BigInteger getPlayerId) {
        this.playerIdField = getPlayerId;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getPinField() {
        return pinField;
    }

    public void setPinField(String pin) {
        this.pinField = pin;
    }

    public String getPersonaName() {
        return personaName;
    }

    public void setPersonaName(String username) {
        this.personaName = username;
    }

    public User(){}
}
