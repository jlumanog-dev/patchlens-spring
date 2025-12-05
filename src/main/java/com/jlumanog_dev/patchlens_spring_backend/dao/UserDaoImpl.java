package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {
    private EntityManager entityManager;

    @Autowired
    public UserDaoImpl(EntityManager entityManager){
        this.entityManager = entityManager;
    }
    @Override
    public User findByPin(Object shaPin){
        TypedQuery<User> query = this.entityManager.createQuery("FROM User where shaLookup=:data", User.class);
        query.setParameter("data", shaPin);
        return query.getSingleResult();
    }
    public void save(User users){
        this.entityManager.persist(users);
    }

    @Override
    public User findByPersona(String persona){
        TypedQuery<User> query = this.entityManager.createQuery("FROM User where personaName=:persona", User.class);
        query.setParameter("persona", persona);
        return query.getSingleResult();
    }

}
