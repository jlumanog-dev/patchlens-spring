package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.RecentMatchAggregateDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.RecentMatchesDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.*;
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
    public Map<String, Object> retrieveRecentMatches(BigInteger steamId){
        RecentMatchesDTO[] recentMatchesDTOList = this.dotaRestTemplate.getForObject(this.api[1] + steamId.toString() + this.apiQueryParams[0], RecentMatchesDTO[].class);
        //aggregate fields
        float winRate;
        float avgKDA;
        float avgGPM;
        float avgXPM;
        float avgHeroDamage;
        float avgTowerDamage;
        float avgLastHit;
        float avgLastHitPerMinute;
        int totalWins = 0;
        int sumKills = 0;
        int sumDeaths = 0;
        int sumAssists = 0;
        int sumGPM = 0;
        int sumXPM = 0;
        int sumHeroDamage = 0;
        int sumTowerDamage = 0;
        int sumLastHits = 0;
        int sumDuration = 0;

        for(RecentMatchesDTO element : recentMatchesDTOList){
            element.setKdaRatio(element.getKills(), element.getDeaths(), element.getAssists());
            element.setGpmXpmEfficiency(element.getGold_per_min(), element.getXp_per_min());
            element.setCsPerMinEfficiency(element.getLast_hits(), element.getDuration());
            element.setHeroDmgEfficiency(element.getHero_damage(), element.getDuration());
            element.setTowerDmgEfficiency(element.getTower_damage(), element.getDuration());
            //if player won match as radiant
            if(element.radiant_win && element.getPlayer_slot() <= 127){
                totalWins += 1;
            }//else if player won match as dire
            else if(!element.radiant_win && element.getPlayer_slot() >= 128){
                //might implement something like radiant-dire win ratio or something similar in the future
                totalWins += 1;
            }
            sumKills += element.getKills();
            sumDeaths += element.getDeaths();
            sumAssists += element.getAssists();
            sumGPM += element.getGold_per_min();
            sumXPM += element.getXp_per_min();
            sumHeroDamage += element.getHero_damage();
            sumTowerDamage += element.getTower_damage();
            sumLastHits += element.getLast_hits();
            sumDuration += element.getDuration();

        }
        System.out.println("total wins: " + totalWins);
        winRate = 100 * ((float) totalWins / recentMatchesDTOList.length);
        avgKDA = (float) (sumKills + sumAssists) / Math.max(1, sumDeaths);
        avgGPM = (float) sumGPM / recentMatchesDTOList.length;
        avgXPM = (float) sumXPM / recentMatchesDTOList.length;
        avgHeroDamage = (float) sumHeroDamage / recentMatchesDTOList.length;
        avgTowerDamage = (float) sumTowerDamage / recentMatchesDTOList.length;
        //farm stats
        avgLastHit = (float) sumLastHits  / recentMatchesDTOList.length;
        avgLastHitPerMinute = (float) sumLastHits / ((float)sumDuration / 60);

        System.out.println("win rate: " + winRate + "\naverage KDA: " +
                avgKDA + "\naverage GPM: " + avgGPM + "\naverage XPM: " + avgXPM + "\naverage LH: "
                + avgLastHit + "\naverage LH per Min: " + avgLastHitPerMinute + "\navg Hero Dmg: " +
                avgHeroDamage + "\nAvg Tower Dmg: " + avgTowerDamage
        );
        Map<String, Object> matchMap = new HashMap<>();
        matchMap.put("match_aggregate",  new RecentMatchAggregateDTO(recentMatchesDTOList.length, winRate, avgKDA, avgGPM, avgXPM, avgHeroDamage, avgTowerDamage, avgLastHit, avgLastHitPerMinute));
        matchMap.put("match_list", recentMatchesDTOList);
        return matchMap;

    }

}
