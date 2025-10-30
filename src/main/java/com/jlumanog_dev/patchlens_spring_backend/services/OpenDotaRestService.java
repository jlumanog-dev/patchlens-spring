package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dao.HeroDao;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


//This service class is dedicated to retrieving game data from OpenDota API
// and persisting it into all relevant database table
@Service
public class OpenDotaRestService {
    private RestTemplate dotaRestTemplate;
    private HeroDao heroDao;
    @Autowired
    public OpenDotaRestService(RestTemplate dotaRestTemplate, HeroDao heroDao){
        this.dotaRestTemplate = dotaRestTemplate;
        this.heroDao = heroDao;
    }

    @Transactional
    public void retrieveAllHeroes(){
        String api = "https://api.opendota.com/api/heroes";
        //send GET request, expecting an array of JSON data and mapping each to an object of type Hero.
        try{
            Hero[] heroes = this.dotaRestTemplate.getForObject(api, Hero[].class);
            for(Hero elem : heroes){
                System.out.println(elem);
            }
            this.heroDao.saveAll(heroes);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}
