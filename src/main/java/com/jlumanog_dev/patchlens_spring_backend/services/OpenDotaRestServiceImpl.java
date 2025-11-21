package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

//This service class is dedicated to retrieving game data from OpenDota API
// and persisting it into all relevant database table
@Service
public class OpenDotaRestServiceImpl implements OpenDotaRestService {
    private RestTemplate dotaRestTemplate;

    //these API endpoints don't need keys at the moment
    private String[] api = {
            "https://api.opendota.com/api/heroStats",
            "https://api.opendota.com/api/players/"
    };


    @Autowired
    public OpenDotaRestServiceImpl(RestTemplate dotaRestTemplate){
        this.dotaRestTemplate = dotaRestTemplate;
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

}
