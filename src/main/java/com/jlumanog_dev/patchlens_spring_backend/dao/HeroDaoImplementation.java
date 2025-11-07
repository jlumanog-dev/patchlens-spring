package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HeroDaoImplementation implements HeroDao {
    private final EntityManager entityManager;
    public HeroDaoImplementation(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    /* Might modify this later for when a new hero is introduced.
    Persist, Merge, or finding a way to append only the specific JSON element
    that doesn't exist on the table yet*/
    @Override
    public void saveAll(Hero[] allHeroes){
        for(Object elem : allHeroes){
            this.entityManager.merge(elem);
        }
    }

    @Override
    public List<Hero> retrieveAllHeroes(){
        TypedQuery<Hero> query = this.entityManager.createQuery("Select hero FROM Hero hero", Hero.class);
        return query.getResultList();
    }

    @Override
    public List<Hero> retrieveTopHeroesStats(){
        TypedQuery<Hero> heroesQuery = this.entityManager.createQuery(
                "SELECT hero FROM Hero hero JOIN FETCH hero.heroStats stats ORDER BY stats.pub_win DESC, stats.pub_pick DESC",
                Hero.class).setMaxResults(3);

        return heroesQuery.getResultList();
    }
    @Override
    public Hero retrieveOneHero(int id){
        //try catch later maybe
        TypedQuery<Hero> query = this.entityManager.createQuery("Select hero FROM Hero hero JOIN FETCH hero.heroStats WHERE hero.id=:data", Hero.class);
        query.setParameter("data", id);
        return query.getSingleResult();
    }
}
