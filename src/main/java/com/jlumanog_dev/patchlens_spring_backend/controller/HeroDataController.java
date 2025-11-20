package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;

import com.jlumanog_dev.patchlens_spring_backend.scheduler.HeroStatsScheduler;
import com.jlumanog_dev.patchlens_spring_backend.services.OpenDotaRestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")
public class HeroDataController {

    private ModelMapper modelMapper;
    private OpenDotaRestService openDotaRestService;
    private HeroStatsScheduler heroStatsScheduler;


    @Autowired
    public HeroDataController(ObjectMapper objectMapper,
                              ModelMapper modelMapper,
                              CacheManager cacheManager,
                              HeroStatsScheduler heroStatsScheduler){
        this.modelMapper = modelMapper;
        this.heroStatsScheduler = heroStatsScheduler;
    }
    //returns list of heroes for search on frontend
    @GetMapping("/all-heroes")
    public ResponseEntity<List<HeroDataDTO>> allHeroes(){
        System.out.println("/all-heroes called");
        List<HeroDataDTO> heroesList = this.heroStatsScheduler.allHeroesStatsRefresh();
        return ResponseEntity.ok(heroesList);
    }
    //top performing heroes with high winrate and pro-to-pub disparity score
    @GetMapping("/top-heroes")
    public ResponseEntity<List<HeroDataDTO>> topPerformingHeroes(){
        System.out.println("/top-heroes called");
        List<HeroDataDTO> topHeroesList = this.heroStatsScheduler.topHeroStatsRefresh();
        return ResponseEntity.ok(topHeroesList);
    }

    //individual hero stats, not user's stats on this hero but maybe later that may be the case
/*    @GetMapping("/{id}")
    public ResponseEntity<HeroDataDTO> getHero(@PathVariable int id){
        HeroDataDTO[] heroesList = this.heroStatsScheduler.allHeroesStatsRefresh();

    }*/

}
