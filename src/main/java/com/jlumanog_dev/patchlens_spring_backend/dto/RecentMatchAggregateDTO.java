package com.jlumanog_dev.patchlens_spring_backend.dto;

public class RecentMatchAggregateDTO {
    //computed fields - all public to serialize them to json because no getters. I believe.
    //Frontend receives empty json if all fields are private AND no getters.
    //Private fields with getters will  private fields to JSON
    public int totalMatches;
    public float winRate;
    public float avgKDA;
    public float avgGPM;
    public float avgXPM;
    public float avgHeroDamage;
    public float avgTowerDamage;
    public float avgLastHit;
    public float avgLastHitPerMinute;



    public RecentMatchAggregateDTO(int totalMatches, float winRate, float avgKDA, float avgGPM, float avgXPM, float avgHeroDamage, float avgTowerDamage, float avgLastHit, float avgLastHitPerMinute) {
        this.totalMatches = totalMatches;
        this.winRate = winRate;
        this.avgKDA = avgKDA;
        this.avgGPM = avgGPM;
        this.avgXPM = avgXPM;
        this.avgHeroDamage = avgHeroDamage;
        this.avgTowerDamage = avgTowerDamage;
        this.avgLastHit = avgLastHit;
        this.avgLastHitPerMinute = avgLastHitPerMinute;
    }
}
