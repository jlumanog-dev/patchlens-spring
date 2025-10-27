package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImplementation implements UserDao {
    private EntityManager entityManager;

    @Autowired
    public UserDaoImplementation(EntityManager entityManager){
        this.entityManager = entityManager;
    }
    public User findByUsername(Object username){
        TypedQuery<User> query = this.entityManager.createQuery("FROM User where username=:data", User.class);
        query.setParameter("data", username);
        return query.getSingleResult();
    }
    public void save(User users){
        this.entityManager.persist(users);
    }
}
