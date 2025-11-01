package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.services.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/heroes")
public class HeroDataController {

    private HeroService heroService;

    @Autowired
    public HeroDataController(HeroService heroService){
        this.heroService = heroService;
    }

    @GetMapping("/top-heroes")
    public ResponseEntity<List<Hero>> topPerformingHeroes(){
        List<Hero> topPerformingHeroesList = this.heroService.retrieveTopHeroesStats();
        for(Hero element : topPerformingHeroesList){
            System.out.println(element);
        }
        return ResponseEntity.ok(topPerformingHeroesList);
    }

}
