package com.jlumanog_dev.patchlens_spring_backend.dto;

public class TopHeroComputedDTO {
    private String localized_name;
    private float averageKills;
    private float averageDeaths;
    private float averageAssists;
    private int numberOfMatches;
    private String[] roles;
    public TopHeroComputedDTO(
            String localized_name,
            float averageKills,
            float averageDeaths,
            float averageAssists,
            int numberOfMatches,
            String[] roles
    ) {
        this.localized_name = localized_name;
        this.averageKills = averageKills;
        this.averageDeaths = averageDeaths;
        this.averageAssists = averageAssists;
        this.numberOfMatches = numberOfMatches;
        this.roles = roles;
    }

    // no-args constructor (needed by some frameworks)
    public TopHeroComputedDTO() {
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public float getAverageKills() {
        return averageKills;
    }

    public void setAverageKills(float averageKills) {
        this.averageKills = averageKills;
    }

    public float getAverageDeaths() {
        return averageDeaths;
    }

    public void setAverageDeaths(float averageDeaths) {
        this.averageDeaths = averageDeaths;
    }

    public float getAverageAssists() {
        return averageAssists;
    }

    public void setAverageAssists(float averageAssists) {
        this.averageAssists = averageAssists;
    }

    public int getNumberOfMatches() {
        return numberOfMatches;
    }

    public void setNumberOfMatches(int numberOfMatches) {
        this.numberOfMatches = numberOfMatches;
    }
}
