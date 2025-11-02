package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.services.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/heroes")
public class HeroDataController {

    private HeroService heroService;

    @Autowired
    public HeroDataController(HeroService heroService, ObjectMapper objectMapper){
        this.heroService = heroService;
    }

    @GetMapping("/top-heroes")
    public ResponseEntity<List<Hero>> topPerformingHeroes(){
        float winRate;

        List<Hero> topPerformingHeroesList = this.heroService.retrieveTopHeroesStats();

        for(Hero element : topPerformingHeroesList){
            //winRate = 100 * ((float) element.getHeroStats().getPub_win() / element.getHeroStats().getPub_pick());
            System.out.println(element);
            //topHeroes.put(element.getId(), element);
        }


        return ResponseEntity.ok(topPerformingHeroesList);
    }

}
