package com.jlumanog_dev.patchlens_spring_backend.dto;

import com.jlumanog_dev.patchlens_spring_backend.entity.HeroStats;

public class HeroDataDTO {
    private int id;
    private String localized_name;
    private String[] roles;
    private String attack_type;
    private String primary_attr;
    private HeroStats heroStats;
    private float winRate;
    private float pickGrowthRateChange;
    private String img;
    private String icon;

    public float getPickGrowthRateChange() {
        return pickGrowthRateChange;
    }

    public void setPickGrowthRateChange(float pickGrowthRateChange) {
        this.pickGrowthRateChange = pickGrowthRateChange;
    }

    public String getPrimary_attr() {
        return primary_attr;
    }

    public void setPrimary_attr(String primary_attr) {
        this.primary_attr = primary_attr;
    }

    public float getWinRate() {
        return winRate;
    }
    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public void setLocalized_name(String localized_name) {
        this.localized_name = localized_name;
    }

    public String getAttack_type() {
        return attack_type;
    }

    public void setAttack_type(String attack_type) {
        this.attack_type = attack_type;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public HeroStats getHeroStats() {
        return heroStats;
    }

    public void setHeroStats(HeroStats heroStats) {
        this.heroStats = heroStats;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
