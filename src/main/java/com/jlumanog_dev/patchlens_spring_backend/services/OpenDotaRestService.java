package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dao.HeroDao;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.entity.HeroStats;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;


//This service class is dedicated to retrieving game data from OpenDota API
// and persisting it into all relevant database table
@Service
public class OpenDotaRestService {
    private RestTemplate dotaRestTemplate;
    private HeroDao heroDao;

    //these API endpoints don't need keys at the moment
    private String[] api = {
            "https://api.opendota.com/api/heroes",
            "https://api.opendota.com/api/heroStats",
            "https://api.opendota.com/api/players/"
    };


    @Autowired
    public OpenDotaRestService(RestTemplate dotaRestTemplate, HeroDao heroDao){
        this.dotaRestTemplate = dotaRestTemplate;
        this.heroDao = heroDao;
    }

    @Transactional
    public void retrieveAllHeroes(){
        //send GET request, expecting an array of JSON data and mapping each to an object of type Hero and HeroStats.
        try{
            Hero[] heroes = this.dotaRestTemplate.getForObject(this.api[0], Hero[].class);
            HeroStats[] heroStats = this.dotaRestTemplate.getForObject(this.api[1], HeroStats[].class);

            //assume both heroes and heroStats have exact same length
           assert Objects.requireNonNull(heroes).length == Objects.requireNonNull(heroStats).length;
            System.out.println("hero size: " + heroes.length);
            System.out.println("hero_stats size: " + heroStats.length);
            //each hero is associated with their respective heroStats
            for(int i = 0; i < heroes.length; i++) {
                /* making sure that the current hero element is associated with the current hero stats element/object
                by checking their localized_name values*/
                System.out.println("from heroes: " + heroes[i].getLocalized_name());
                System.out.println("from herostats: " + heroStats[i].getLocalized_name());
                System.out.println("ID: " + heroes[i].getId());
                System.out.println("Hero Stat ID: " + heroStats[i].getId());
                if (heroes[i].getLocalized_name().equals(heroStats[i].getLocalized_name())) {
                    heroes[i].setHeroStats(heroStats[i]); //this will make sure to cascade persist operation of heroStats object for HeroStats entity
                }else if(heroes[i].getId() == heroStats[i].getId()){
                    //This is for cases where localized Hero names are not the same
                    //OD and Ring master have different names
                    heroes[i].setHeroStats(heroStats[i]);
                }else{
                    System.out.println("NOT MATCH");
                }
            }
            this.heroDao.saveAll(heroes); // persist an array of Heroes. Should also perform cascade persist/merge

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public HeroesPlayedByUserDTO[] retrieveHeroesPlayed(BigInteger steamId){
        HeroesPlayedByUserDTO[] playedByUserDTO = this.dotaRestTemplate.getForObject(this.api[2] + steamId.toString() + "/heroes", HeroesPlayedByUserDTO[].class);
        return playedByUserDTO;
    }


}
