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
            pickRateChanges = ( 100 *  ( ( ( (float) latest_pub_pick_trend - (float) oldest_pub_pick_trend)) / (float) oldest_pub_pick_trend ) );
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

        int pub_pick_trend_size = hero.getHeroStats().getPub_pick_trend().length;
        int pub_win_trend_size = hero.getHeroStats().getPub_win_trend().length;

        int latest_pub_pick_trend = hero.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 2]; // -2 to get the second last element. might change later to use the latest live data;
        int oldest_pub_pick_trend = hero.getHeroStats().getPub_pick_trend()[0];
        int latest_pub_win_trend = hero.getHeroStats().getPub_win_trend()[pub_pick_trend_size - 2]; // -2 to get the second last element. might change later to use the latest live data;
        int oldest_pub_win_trend = hero.getHeroStats().getPub_win_trend()[0];

        //subtract the 7th item out of the total sum of total win and pick
        int pub_win_total = hero.getHeroStats().getPub_win() - hero.getHeroStats().getPub_win_trend()[pub_win_trend_size - 1];
        int pub_pick_total = hero.getHeroStats().getPub_pick() - hero.getHeroStats().getPub_pick_trend()[pub_pick_trend_size - 1];

        float winRate = (100 *  ( (float) pub_win_total / pub_pick_total));
        float pickRateChanges = (100 * ((float) (latest_pub_pick_trend - oldest_pub_pick_trend) / oldest_pub_pick_trend));
        float winRateChanges = (100 * ((float) (latest_pub_win_trend - oldest_pub_win_trend) / oldest_pub_win_trend));



        //STANDARD DEVIATION PROCESS START HERE
        /* again, excluding the 7th day because it's the on-constant data that
        always updates. Might change later */

        System.out.println("pub total win minus last element: " + pub_win_total);
        double averageWin = (double) (pub_win_total / 6);

        /*
        1. compute the difference between each pub wins per day and the average win value
        2. get the square of each difference and get the total sum of it all.
        3. divide the total sum by 6 (6 days)
        4. get the square root of the quotient.
        Needs to be BigDecimal because java basic data type can't return precise large value, instead just return concise values
        */
        ArrayList<BigDecimal> deviationList = new ArrayList<>();
        BigDecimal standardDeviation = new BigDecimal("0.0");

        for (int i = 0; i < hero.getHeroStats().getPub_win_trend().length - 1; i++) {
            BigDecimal difference =  BigDecimal.valueOf(hero.getHeroStats().getPub_win_trend()[i] - averageWin);
            System.out.println("difference: " + difference);

            System.out.println("squared: " + difference.pow(2));
            standardDeviation = standardDeviation.add(difference.pow(2));
        }

        System.out.println(standardDeviation);
        //MathContext is important for getting precise digits & decimal, and rounding rules
        MathContext mc = new MathContext(8, RoundingMode.HALF_UP);
        standardDeviation = standardDeviation.divide(BigDecimal.valueOf(6), mc); // Divide the total sum to 6 (6 days)
        standardDeviation = standardDeviation.sqrt(mc); //get square root of the current 'standardDeviation' value.

        HeroDataDTO heroDataDTO = this.modelMapper.map(hero, HeroDataDTO.class);
        heroDataDTO.setWinRate(winRate);
        heroDataDTO.setPickGrowthRateChange(pickRateChanges);
        heroDataDTO.setWinGrowthRateChange(winRateChanges);
        heroDataDTO.setTrendStdDev(standardDeviation);
        return ResponseEntity.ok(heroDataDTO);
    }



}
