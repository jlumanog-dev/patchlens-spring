package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.dao.UserDao;
import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImplementation implements UserService{
    private UserDao userDao;

    @Autowired
    public UserServiceImplementation(UserDao userDao){
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void save(User users){
        this.userDao.save(users);
    }
    @Override
    public User findByPin(Object shaPin){
        return this.userDao.findByPin(shaPin);
    }
    @Override
    public User findByPersona(String persona){
        return this.userDao.findByPersona(persona);
    }

}
