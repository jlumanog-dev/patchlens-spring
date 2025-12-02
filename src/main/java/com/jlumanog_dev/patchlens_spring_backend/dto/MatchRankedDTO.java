package com.jlumanog_dev.patchlens_spring_backend.dto;

public class MatchRankedDTO {
    public long match_id;
    public int player_slot;
    public boolean radiant_win;
    public int game_mode;
    public int duration;
    public int lobby_type;
    public int hero_id;
    public long start_time;
    public Integer version;       // nullable
    public int kills;
    public int deaths;
    public int assists;
    public int average_rank;
    public int leaver_status;
    public Integer party_size;    // nullable
    public int hero_variant;

    private String img;
    private String[] roles;
    private String localized_name;


    public void setImg(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }

    public int getHero_id() {
        return hero_id;
    }
}
