package com.jlumanog_dev.patchlens_spring_backend.entity;

import jakarta.persistence.*;


/*
NOTE:
Entity field names need to be exactly as the named properties in JSON data
so that in OpenDotaRestService, it can deserialize each JSON data to Hero Entity
 */

@Entity
@Table(name = "heroes")
public class Hero {
    public Hero(){}

    @OneToOne(cascade = {CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.PERSIST,
            CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_stats_id")
    public HeroStats heroStats;

    @Id
    @Column(name="id")
    private int id;

    @Column(name="name")
    private String name;

    @Column(name="localized_name")
    private String localized_name;

    @Column(name="primary_attr")
    private String primary_attr;

    @Column(name="attack_type")
    private String attack_type;

    @Column(name="roles")
    private String[] roles;

    @Column(name = "legs")
    private int legs;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLocalized_name() {
        return localized_name;
    }

    public String getPrimary_attr() {
        return primary_attr;
    }

    public String getAttack_type() {
        return attack_type;
    }

    public int getLegs() {
        return legs;
    }

    public String[] getRoles() {
        return roles;
    }

    public HeroStats getHeroStats() {
        return heroStats;
    }

    public void setHeroStats(HeroStats heroStats) {
        this.heroStats = heroStats;
    }

    @Override
    public String toString() {
        return "ID: " + this.getId() + "\nName: " + this.getName() + "\nRoles: " + this.getRoles() + "\nStats on Pub Win: " + this.getHeroStats();
    }
}
