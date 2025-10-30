package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.jlumanog_dev.patchlens_spring_backend.dao.HeroDao;
import com.jlumanog_dev.patchlens_spring_backend.services.OpenDotaRestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/opendota")
public class HeroController {

    private OpenDotaRestService openDotaRestService;

    public HeroController(OpenDotaRestService openDotaRestService){
        this.openDotaRestService = openDotaRestService;
    }

    @GetMapping("/heroes")
    public Map<String, Object> getHeroesFromApi(){
        Map<String, Object> response = new HashMap<>();
        try{
            this.openDotaRestService.retrieveAllHeroes();
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }
        response.put("message", "persisted all heroes' data");
        return response;
    }
}
