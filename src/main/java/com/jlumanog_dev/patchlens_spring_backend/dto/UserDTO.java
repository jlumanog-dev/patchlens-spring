package com.jlumanog_dev.patchlens_spring_backend.dto;

import jakarta.persistence.Column;

import java.math.BigInteger;

public class UserDTO {
    private String username;
    private String email;
    private String role;
    private BigInteger steamId;

    public BigInteger getSteamId() {
        return steamId;
    }

    public void setSteamId(BigInteger steamId) {
        this.steamId = steamId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
