package com.jlumanog_dev.patchlens_spring_backend.dto;
import java.math.BigDecimal;

public class HeroDataDTO {
    private int id;
    private String localized_name;
    private String[] roles;
    private String attack_type;
    private String primary_attr;

    private int base_str;
    private int base_agi;
    private int base_int;
    private int move_speed;

    int[] pub_pick_trend;
    int[] pub_win_trend;
    int pub_pick;
    int pub_win;
    int pro_pick;
    int pro_win;

    private float winRate;
    private float pickGrowthRateChange;
    private float winGrowthRateChange;
    private BigDecimal trendStdDev;
    private float disparityScore;
    private String img;
    private String icon;

    public int getBase_str() {
        return base_str;
    }

    public void setBase_str(int base_str) {
        this.base_str = base_str;
    }

    public int getBase_int() {
        return base_int;
    }

    public void setBase_int(int base_int) {
        this.base_int = base_int;
    }

    public int getBase_agi() {
        return base_agi;
    }

    public void setBase_agi(int base_agi) {
        this.base_agi = base_agi;
    }

    public int getMove_speed() {
        return move_speed;
    }

    public void setMove_speed(int move_speed) {
        this.move_speed = move_speed;
    }

    public int getPub_win() {
        return pub_win;
    }

    public void setPub_win(int pub_win) {
        this.pub_win = pub_win;
    }

    public int getPub_pick() {
        return pub_pick;
    }

    public void setPub_pick(int pub_pick) {
        this.pub_pick = pub_pick;
    }

    public int getPro_pick() {
        return pro_pick;
    }

    public void setPro_pick(int pro_pick) {
        this.pro_pick = pro_pick;
    }

    public int getPro_win() {
        return pro_win;
    }

    public void setPro_win(int pro_win) {
        this.pro_win = pro_win;
    }

    public int[] getPub_pick_trend() {
        return pub_pick_trend;
    }

    public void setPub_pick_trend(int[] pub_pick_trend) {
        this.pub_pick_trend = pub_pick_trend;
    }

    public int[] getPub_win_trend() {
        return pub_win_trend;
    }

    public void setPub_win_trend(int[] pub_win_trend) {
        this.pub_win_trend = pub_win_trend;
    }

    public float getDisparityScore() {
        return disparityScore;
    }

    public void setDisparityScore(float disparityScore) {
        this.disparityScore = disparityScore;
    }

    public BigDecimal getTrendStdDev() {
        return trendStdDev;
    }

    public void setTrendStdDev(BigDecimal trendStdDev) {
        this.trendStdDev = trendStdDev;
    }

    public double getWinGrowthRateChange() {
        return winGrowthRateChange;
    }

    public void setWinGrowthRateChange(float winGrowthRateChange) {
        this.winGrowthRateChange = winGrowthRateChange;
    }

    public float getPickGrowthRateChange() {
        return pickGrowthRateChange;
    }

    public void setPickGrowthRateChange(float pickGrowthRateChange) {
        this.pickGrowthRateChange = pickGrowthRateChange;
    }
    public float getWinRate() {
        return winRate;
    }
    public void setWinRate(float winRate) {
        this.winRate = winRate;
    }

    public String getPrimary_attr() {
        return primary_attr;
    }

    public void setPrimary_attr(String primary_attr) {
        this.primary_attr = primary_attr;
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
