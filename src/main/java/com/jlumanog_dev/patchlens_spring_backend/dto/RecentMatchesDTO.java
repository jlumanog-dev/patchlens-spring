package com.jlumanog_dev.patchlens_spring_backend.dto;

public class RecentMatchesDTO {
    private long match_id;
    private int player_slot;
    public boolean radiant_win;
    private int hero_id;
    private long start_time;
    private int duration;
    private int game_mode;
    private int lobby_type;
    private int kills;
    private int deaths;
    private int assists;
    private int average_rank;
    private int xp_per_min;
    private int gold_per_min;
    private int hero_damage;
    private int tower_damage;
    private int hero_healing;
    private int last_hits;
    private int cluster;
    private int hero_variant;

    private float kdaRatio;
    private float gpmXpmEfficiency;
    private float csPerMinEfficiency;
    private float heroDmgEfficiency;
    private float towerDmgEfficiency;

    public RecentMatchesDTO(){
        this.kdaRatio = kdaRatio;
        this.gpmXpmEfficiency = gpmXpmEfficiency;
        this.csPerMinEfficiency = csPerMinEfficiency;
        this.heroDmgEfficiency = heroDmgEfficiency;
        this.towerDmgEfficiency = towerDmgEfficiency;
    }

    public long getMatch_id() {
        return match_id;
    }

    public int getPlayer_slot() {
        return player_slot;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getGold_per_min() {
        return gold_per_min;
    }

    public int getXp_per_min() {
        return xp_per_min;
    }

    public int getDuration() {
        return duration;
    }

    public int getLast_hits() {
        return last_hits;
    }

    public int getHero_damage() {
        return hero_damage;
    }

    public int getTower_damage() {
        return tower_damage;
    }

    public float getKdaRatio() {
        return kdaRatio;
    }

    public void setKdaRatio(int kill, int death, int assist) {
        this.kdaRatio = (float) (kill / assist) / Math.max(1, death);
    }

    public float getGpmXpmEfficiency() {
        return gpmXpmEfficiency;
    }

    public void setGpmXpmEfficiency(int gpm, int xpm) {
        this.gpmXpmEfficiency = (float) gpm / xpm;
    }

    public float getCsPerMinEfficiency() {
        return csPerMinEfficiency;
    }

    public void setCsPerMinEfficiency(int lastHit, int duration) {
        this.csPerMinEfficiency = (float) lastHit / ( (float) duration / 60);
    }

    public float getHeroDmgEfficiency() {
        return heroDmgEfficiency;
    }

    public void setHeroDmgEfficiency(int heroDamage, int duration) {
        this.heroDmgEfficiency = (float) heroDamage / ((float) duration / 60);
    }

    public float getTowerDmgEfficiency() {
        return towerDmgEfficiency;
    }

    public void setTowerDmgEfficiency(int towerDamage, int duration) {
        this.towerDmgEfficiency = (float) towerDamage / ((float) duration / 60);;
    }
}
