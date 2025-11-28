package com.jlumanog_dev.patchlens_spring_backend.dto;

public class RecentMatchAggregateDTO {
    //computed fields
    private int totalMatches;
    private float winRate;
    private float avgKDA;
    private float avgGPM;
    private float avgXPM;
    private float avgHeroDamage;
    private float avgTowerDamage;
    public float avgLastHit;
    private float avgLastHitPerMinute;



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
