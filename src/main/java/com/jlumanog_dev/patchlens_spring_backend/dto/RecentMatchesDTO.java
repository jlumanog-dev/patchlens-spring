package com.jlumanog_dev.patchlens_spring_backend.dto;

public class RecentMatchesDTO {
    public long match_id;
    public int player_slot;
    public boolean radiant_win;
    public int hero_id;
    public long start_time;
    public int duration;
    public int game_mode;
    public int lobby_type;
    public int kills;
    public int deaths;
    public int assists;
    public int average_rank;
    public int xp_per_min;
    public int gold_per_min;
    public int hero_damage;
    public int tower_damage;
    public int hero_healing;
    public int last_hits;
    public int cluster;
    public int hero_variant;

    public float kdaRatio;
    public float gpmXpmEfficiency;
    public float csPerMinEfficiency;
    public float heroDmgEfficiency;
    public float towerDmgEfficiency;

    public RecentMatchesDTO(){
        this.kdaRatio = kdaRatio;
        this.gpmXpmEfficiency = gpmXpmEfficiency;
        this.csPerMinEfficiency = csPerMinEfficiency;
        this.heroDmgEfficiency = heroDmgEfficiency;
        this.towerDmgEfficiency = towerDmgEfficiency;
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
