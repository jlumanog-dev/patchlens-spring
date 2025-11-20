package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;

import java.math.BigInteger;
import java.util.List;

public interface OpenDotaRestService {
    /*public HeroDataDTO retrieveAHero(int id);*/
    public List<HeroDataDTO> retrieveAllHeroes();
    public HeroesPlayedByUserDTO[] retrieveHeroesPlayed(BigInteger steamId);
}