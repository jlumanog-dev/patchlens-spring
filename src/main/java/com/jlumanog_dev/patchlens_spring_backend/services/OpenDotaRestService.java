package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.MatchRankedDTO;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface OpenDotaRestService {
    /*public HeroDataDTO retrieveAHero(int id);*/
    public List<HeroDataDTO> retrieveAllHeroes();
    public  Map<String, Object>  retrieveHeroesPlayed(BigInteger steamId);
    public HeroDataDTO retrieveHero(int heroId);
    public Map<String, Object> retrieveRecentMatches(BigInteger steamId);
}