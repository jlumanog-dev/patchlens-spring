package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroBasicDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroDataDTO;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.services.HeroService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/heroes")
public class HeroDataController {

    private HeroService heroService;
    private ModelMapper modelMapper;

    @Autowired
    public HeroDataController(HeroService heroService, ObjectMapper objectMapper,ModelMapper modelMapper){
        this.heroService = heroService;
        this.modelMapper = modelMapper;
    }
    @GetMapping("/all-heroes")
    public ResponseEntity<List<HeroBasicDataDTO>> allHeroes(){
        List<Hero> allHeroes = this.heroService.retrieveAllHeroes();
        List<HeroBasicDataDTO> allHeroesMappedDTO = new ArrayList<>();
/*        Need to use DTO and map Hero to HeroBasicDataDTO because Jackson
        library cannot serialize lazy-loaded entity object that is stil
        considered "JPA-Managed entity"*/
        for (Hero element : allHeroes){
            HeroBasicDataDTO heroBasic = this.modelMapper.map(element, HeroBasicDataDTO.class);
            allHeroesMappedDTO.add(heroBasic);
        }
        return ResponseEntity.ok(allHeroesMappedDTO);
    }

    @GetMapping("/top-heroes")
    public ResponseEntity<List<HeroDataDTO>> topPerformingHeroes(){

        List<Hero> topPerformingHeroesList = this.heroService.retrieveTopHeroesStats();
        List<HeroDataDTO> HeroDTOList = new ArrayList<>();
        float winRate;

        int latest_pub_pick_trend;
        int oldest_pub_pick_trend;
        int pub_pick_trend_size;
        float pickRateChanges;

        for(Hero element : topPerformingHeroesList){
            pub_pick_trend_size = element.getHeroStats().getPub_pick_trend().length;

            //getting the top pick on previous day, not the current day
            latest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2];
            oldest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[0];


            winRate = (100 * ((float) element.getHeroStats().getPub_win() / (float) element.getHeroStats().getPub_pick()));

            // ((prev_latest − earliest) / earliest) × 100
            pickRateChanges = ( 100 *  ( (float) latest_pub_pick_trend - (float) oldest_pub_pick_trend) / (float) oldest_pub_pick_trend);
            HeroDataDTO insightDTO = this.modelMapper.map(element, HeroDataDTO.class);
            insightDTO.setWinRate(winRate);
            insightDTO.setPickGrowthRateChange(pickRateChanges);
            HeroDTOList.add(insightDTO);
        }
        return ResponseEntity.ok(HeroDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroDataDTO> getHero(@PathVariable int id){
        Hero hero = this.heroService.retrieveOneHero(id);
        HeroDataDTO heroDataDTO = this.modelMapper.map(hero, HeroDataDTO.class);
        return ResponseEntity.ok(heroDataDTO);
    }



}
