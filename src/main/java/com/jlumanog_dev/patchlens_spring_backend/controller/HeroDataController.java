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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
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
        library cannot serialize lazy-loaded entity object that is still
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


        for(Hero element : topPerformingHeroesList){
            int pub_pick_trend_size = element.getHeroStats().getPub_pick_trend().length;

            //getting the top pick on previous day, not the current day
            int latest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2];
            int oldest_pub_pick_trend = element.getHeroStats().getPub_pick_trend()[0];

            float pubWinRate = this.winRateMethod(element.getHeroStats().getPub_win(), element.getHeroStats().getPub_pick());
            float proWinRate = this.winRateMethod(element.getHeroStats().getPro_win(), element.getHeroStats().getPro_pick());
            // ((prev_latest − earliest) / earliest) × 100
            float pickRateChanges = this.growthRateMethod(latest_pub_pick_trend, oldest_pub_pick_trend);

            float disparityScore = this.disparityScore(proWinRate, pubWinRate);

            double averageWin = (double) (element.getHeroStats().getPub_win() / 6);

            HeroDataDTO insightDTO = this.modelMapper.map(element, HeroDataDTO.class);
            insightDTO.setWinRate(pubWinRate);
            insightDTO.setPickGrowthRateChange(pickRateChanges);
            insightDTO.setTrendStdDev(this.standardDeviationMethod(element, averageWin));
            insightDTO.setDisparityScore(disparityScore);

            HeroDTOList.add(insightDTO);
        }
        return ResponseEntity.ok(HeroDTOList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroDataDTO> getHero(@PathVariable int id){
        Hero hero = this.heroService.retrieveOneHero(id);

        /* Metrics for Pub Ranked Games */
        int pub_pick_trend_size = hero.getHeroStats().getPub_pick_trend().length;
        int pub_win_trend_size = hero.getHeroStats().getPub_win_trend().length;

        int latest_pub_pick_trend = hero.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2]; // -2 to get the second last element. might change later to use the latest live data;
        int oldest_pub_pick_trend = hero.getHeroStats().getPub_pick_trend()[0];
        int latest_pub_win_trend = hero.getHeroStats().getPub_win_trend()[pub_pick_trend_size - 2]; // -2 to get the second last element. might change later to use the latest live data;
        int oldest_pub_win_trend = hero.getHeroStats().getPub_win_trend()[0];

        //subtract the 7th item out of the total sum of total win and pick
        int pub_win_total = hero.getHeroStats().getPub_win() - hero.getHeroStats().getPub_win_trend()[pub_win_trend_size - 2];
        int pub_pick_total = hero.getHeroStats().getPub_pick() - hero.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2];

        float pubWinRate = this.winRateMethod(pub_win_total, pub_pick_total);
        float pickRateChanges = this.growthRateMethod(latest_pub_pick_trend, oldest_pub_pick_trend);
        float winRateChanges = this.growthRateMethod(latest_pub_win_trend, oldest_pub_win_trend);

        /*Metrics for Pro/Official Tournament Games*/
        int pro_pick_total = hero.getHeroStats().getPro_pick();
        int pro_win_total = hero.getHeroStats().getPro_win();

        float proWinRate = this.winRateMethod(pro_win_total, pro_pick_total);

        float disparityScore = this.disparityScore(proWinRate, pubWinRate);
        System.out.println("Pro: " + proWinRate + "\nPub: " + pubWinRate);
        //STANDARD DEVIATION PROCESS START HERE
        /* again, excluding the 7th day because it's the on-constant data that
        always updates. Might change later */

        System.out.println("pub total win minus last element: " + pub_win_total);
        double averageWin = (double) (pub_win_total / 6);

        BigDecimal standardDeviation = this.standardDeviationMethod(hero, averageWin);

        HeroDataDTO heroDataDTO = this.modelMapper.map(hero, HeroDataDTO.class);
        heroDataDTO.setWinRate(pubWinRate);
        heroDataDTO.setPickGrowthRateChange(pickRateChanges);
        heroDataDTO.setWinGrowthRateChange(winRateChanges);
        heroDataDTO.setTrendStdDev(standardDeviation);
        heroDataDTO.setDisparityScore(disparityScore);
        return ResponseEntity.ok(heroDataDTO);
    }


    /*
        1. compute the difference between each pub wins per day and the average win value
        2. get the square of each difference and get the total sum of it all.
        3. divide the total sum by 6 (6 days)
        4. get the square root of the quotient.
        Needs to be BigDecimal because java basic data type can't return precise large value, instead just return concise values
    */
    public BigDecimal standardDeviationMethod(Hero hero, double averageWin ){
        ArrayList<BigDecimal> deviationList = new ArrayList<>();
        BigDecimal standardDeviation = new BigDecimal("0.0");

        for (int i = 0; i < hero.getHeroStats().getPub_win_trend().length; i++) {
            BigDecimal difference =  BigDecimal.valueOf(hero.getHeroStats().getPub_win_trend()[i] - averageWin);
            standardDeviation = standardDeviation.add(difference.pow(2));
        }

        System.out.println(standardDeviation);
        //MathContext is important for getting precise digits & decimal, and rounding rules
        MathContext mc = new MathContext(8, RoundingMode.HALF_UP);
        standardDeviation = standardDeviation.divide(BigDecimal.valueOf(6), mc); // Divide the total sum to 6 (6 days)
        standardDeviation = standardDeviation.sqrt(mc); //get square root of the current 'standardDeviation' value.

        return standardDeviation;
    }
    public float winRateMethod(int win_total, int pick_total){
        return (100 *  ( (float) win_total / pick_total));
    }
    public float growthRateMethod(int latest_pick_trend, int oldest_pick_trend){
        return (100 * ((float) (latest_pick_trend - oldest_pick_trend) / oldest_pick_trend));
    }
    public float disparityScore(float proWinRate, float pubWinRate){
        return proWinRate - pubWinRate;
    }

}
