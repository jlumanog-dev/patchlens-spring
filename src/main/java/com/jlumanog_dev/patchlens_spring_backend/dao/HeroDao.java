package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;

import java.util.List;

public interface HeroDao {
    public void saveAll(Hero[] allHeroes);
    public List<Hero> retrieveTopHeroesStats();
    public List<Hero> retrieveAllHeroes();
}


