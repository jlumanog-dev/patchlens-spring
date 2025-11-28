package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.RecentMatchAggregateDTO;

import java.math.BigInteger;
import java.util.List;

public interface OpenDotaRestService {
    /*public HeroDataDTO retrieveAHero(int id);*/
    public List<HeroDataDTO> retrieveAllHeroes();
    public List<HeroesPlayedByUserDTO> retrieveHeroesPlayed(BigInteger steamId);
    public HeroDataDTO retrieveHero(int heroId);
    public RecentMatchAggregateDTO retrieveRecentMatches(BigInteger steamId);
}