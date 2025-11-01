package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;

import java.util.List;

public interface HeroService {
    public List<Hero> retrieveTopHeroesStats();
}