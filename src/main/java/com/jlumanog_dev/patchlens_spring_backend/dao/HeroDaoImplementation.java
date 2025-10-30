package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

@Repository
public class HeroDaoImplementation implements HeroDao {
    private final EntityManager entityManager;
    public HeroDaoImplementation(EntityManager entityManager){
        this.entityManager = entityManager;
    }
    public void saveAll(Hero[] allHeroes){
        for(Object elem : allHeroes){
            this.entityManager.merge(elem);
        }
    }
}
