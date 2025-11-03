package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlumanog_dev.patchlens_spring_backend.dto.HeroInsightDTO;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.services.HeroService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public HeroDataController(HeroService heroService, ObjectMapper objectMapper, ModelMapper modelMapper){
        this.heroService = heroService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/top-heroes")
    public ResponseEntity<List<HeroInsightDTO>> topPerformingHeroes(){

        List<Hero> topPerformingHeroesList = this.heroService.retrieveTopHeroesStats();
        List<HeroInsightDTO> insightDTOList = new ArrayList<>();
        float winRate;

        int latest_pub_pick_trend;
        int oldest_pub_pick_trend;
        int pub_pick_trend_size;
        float pickRateChanges;

        for(Hero element : topPerformingHeroesList){
            winRate = (100 * ((float) element.getHeroStats().getPub_win() / (float) element.getHeroStats().getPub_pick()));
            pub_pick_trend_size = element.getHeroStats().getPub_pick_trend().length;

            //getting the top pick on previous day, not the current day
            latest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2];
            oldest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[0];

            // ((prev_latest − earliest) / earliest) × 100
            pickRateChanges = ( 100 *  ( (float) latest_pub_pick_trend - (float) oldest_pub_pick_trend) / (float) oldest_pub_pick_trend);
            System.out.println("Pick Rate Change: " + pickRateChanges);
            HeroInsightDTO insightDTO = this.modelMapper.map(element, HeroInsightDTO.class);
            insightDTO.setWinRate(winRate);
            insightDTO.setPickRateChange(pickRateChanges);
            insightDTOList.add(insightDTO);
            //topHeroes.put(element.getId(), element);
        }


        return ResponseEntity.ok(insightDTOList);
    }

}
