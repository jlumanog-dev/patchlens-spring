package com.jlumanog_dev.patchlens_spring_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hero_stats")
public class HeroStats {
    @Id
    private int id;

    public HeroStats(){}

    @Override
    public String toString(){
        return String.valueOf(this.getPub_win());
    }

    @Column(name = "localized_name")
    private String localized_name;

    @Column(name = "base_str")
    private int base_str;

    @Column(name = "base_agi")
    private int base_agi;

    @Column(name = "base_int")
    private int base_int;

    @Column(name = "str_gain")
    private Float str_gain;

    @Column(name = "agi_gain")
    private Float agi_gain;

    @Column(name = "int_gain")
    private Float int_gain;

    @Column(name = "move_speed")
    private int move_speed;

    @Column(name = "base_attack_min")
    private int base_attack_min;

    @Column(name = "base_attack_max")
    private int base_attack_max;

    @Column(name = "base_armor")
    private Float base_armor;

    @Column(name = "pro_pick")
    private int pro_pick;

    @Column(name = "pro_win")
    private int pro_win;

    @Column(name = "pro_ban")
    private int pro_ban;

    @Column(name = "pub_pick")
    private int pub_pick;

    @Column(name = "pub_pick_trend")
    private int[] pub_pick_trend;

    @Column(name = "pub_win")
    private int pub_win;

    @Column(name = "pub_win_trend")
    private int[] pub_win_trend;

    @Column(name = "img")
    private String img;

    @Column(name = "icon")
    private String icon;

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLocalized_name() { return localized_name; }
    public void setLocalized_name(String localized_name) { this.localized_name = localized_name; }

    public int getBase_str() { return base_str; }
    public void setBase_str(int base_str) { this.base_str = base_str; }

    public int getBase_agi() { return base_agi; }
    public void setBase_agi(int base_agi) { this.base_agi = base_agi; }

    public int getBase_int() { return base_int; }
    public void setBase_int(int base_int) { this.base_int = base_int; }

    public Float getStr_gain() { return str_gain; }
    public void setStr_gain(Float str_gain) { this.str_gain = str_gain; }

    public Float getAgi_gain() { return agi_gain; }
    public void setAgi_gain(Float agi_gain) { this.agi_gain = agi_gain; }

    public Float getInt_gain() { return int_gain; }
    public void setInt_gain(Float int_gain) { this.int_gain = int_gain; }

    public int getMove_speed() { return move_speed; }
    public void setMove_speed(int move_speed) { this.move_speed = move_speed; }

    public int getBase_attack_min() { return base_attack_min; }
    public void setBase_attack_min(int base_attack_min) { this.base_attack_min = base_attack_min; }

    public int getBase_attack_max() { return base_attack_max; }
    public void setBase_attack_max(int base_attack_max) { this.base_attack_max = base_attack_max; }

    public Float getBase_armor() { return base_armor; }
    public void setBase_armor(Float base_armor) { this.base_armor = base_armor; }

    public int getPro_pick() { return pro_pick; }
    public void setPro_pick(int pro_pick) { this.pro_pick = pro_pick; }

    public int getPro_win() { return pro_win; }
    public void setPro_win(int pro_win) { this.pro_win = pro_win; }

    public int getPro_ban() { return pro_ban; }
    public void setPro_ban(int pro_ban) { this.pro_ban = pro_ban; }

    public int getPub_pick() { return pub_pick; }
    public void setPub_pick(int pub_pick) { this.pub_pick = pub_pick; }

    public int[] getPub_pick_trend() { return pub_pick_trend; }
    public void setPub_pick_trend(int[] pub_pick_trend) { this.pub_pick_trend = pub_pick_trend; }

    public int getPub_win() { return pub_win; }
    public void setPub_win(int pub_win) { this.pub_win = pub_win; }

    public int[] getPub_win_trend() { return pub_win_trend; }
    public void setPub_win_trend(int[] pub_win_trend) { this.pub_win_trend = pub_win_trend; }


    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}