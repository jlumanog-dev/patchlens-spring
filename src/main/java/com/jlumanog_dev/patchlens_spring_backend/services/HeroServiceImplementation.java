package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dao.HeroDao;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HeroServiceImplementation implements HeroService{

    private HeroDao heroDao;

    @Autowired
    public HeroServiceImplementation(HeroDao heroDao){
        this.heroDao = heroDao;
    }
    public List<Hero> retrieveTopHeroesStats(){
        return this.heroDao.retrieveTopHeroesStats();
    }

}
