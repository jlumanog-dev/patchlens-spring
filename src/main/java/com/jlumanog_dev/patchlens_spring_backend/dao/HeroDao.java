package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;

public interface HeroDao {
    public void saveAll(Hero[] allHeroes);
}
