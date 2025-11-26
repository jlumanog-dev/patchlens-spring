package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.RecentMatchesDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

//This service class is dedicated to retrieving game data from OpenDota API
// and persisting it into all relevant database table
@Service
public class OpenDotaRestServiceImpl implements OpenDotaRestService {
    private RestTemplate dotaRestTemplate;
    private CacheManager cacheManager;
    //these API endpoints don't need keys at the moment
    private String[] api = {
            "https://api.opendota.com/api/heroStats",
            "https://api.opendota.com/api/players/"
    };
    private String[] apiQueryParams = { "/recentMatches?game_mode=22" };


    @Autowired
    public OpenDotaRestServiceImpl(RestTemplate dotaRestTemplate, CacheManager cacheManager, ModelMapper modelMapper){
        this.dotaRestTemplate = dotaRestTemplate;
        this.cacheManager = cacheManager;
    }
    @Override
    public List<HeroDataDTO> retrieveAllHeroes(){
        //send GET request, expecting an array of JSON data and mapping each to an DTO object (HeroDataDTO)
        List<HeroDataDTO> heroesList;
        try{
            HeroDataDTO[] heroes = this.dotaRestTemplate.getForObject(this.api[0], HeroDataDTO[].class);
            assert heroes != null;
            heroesList = Arrays.asList(heroes);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
        return heroesList;
    }

    @Override
    public List<HeroesPlayedByUserDTO> retrieveHeroesPlayed(BigInteger steamId){
        List<HeroesPlayedByUserDTO> heroesPlayedList;
        try{
            HeroesPlayedByUserDTO[] heroesPlayedArray = this.dotaRestTemplate.getForObject(this.api[1] + steamId.toString() + "/heroes", HeroesPlayedByUserDTO[].class);
            assert heroesPlayedArray != null;
            heroesPlayedList = Arrays.asList(heroesPlayedArray);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return heroesPlayedList;
    }

    @Override
    public HeroDataDTO retrieveHero(int heroId){
        CaffeineCache allHeroesCache = (CaffeineCache) this.cacheManager.getCache("allHeroesStatsCache");
        assert allHeroesCache != null;

/*        allHeroesCache.getNativeCache().asMap().forEach((key, value) ->{
            Optional<HeroDataDTO> temp = ((List<HeroDataDTO>) value)
                    .stream().filter(hero -> hero.getId() == heroId).findFirst();
            assert temp.isPresent();
            heroRetrievedDTO = temp;
        });*/
        List<HeroDataDTO> allHeroesList = (List<HeroDataDTO>) allHeroesCache.getNativeCache().asMap().entrySet().iterator().next().getValue();
        return allHeroesList.stream().filter(element -> element.getId() == heroId).findFirst().get();
    }

    @Override
    public void retrieveRecentMatches(BigInteger steamId){
        RecentMatchesDTO[] recentMatchesDTOList = this.dotaRestTemplate.getForObject(this.api[1] + steamId.toString() + this.apiQueryParams[0], RecentMatchesDTO[].class);
        double kdaRatio;

        for(RecentMatchesDTO element : recentMatchesDTOList){

        }

    }

}
