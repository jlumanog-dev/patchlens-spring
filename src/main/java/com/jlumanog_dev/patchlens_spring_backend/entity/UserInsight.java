package com.jlumanog_dev.patchlens_spring_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user_insight")
public class UserInsight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="insight")
    private String insight;

    public UserInsight(){

    }
    public UserInsight(String insight){
    this.insight = insight;
    }
}
