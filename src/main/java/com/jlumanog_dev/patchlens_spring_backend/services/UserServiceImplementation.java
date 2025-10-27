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
    public User findByUsername(Object username){
        User user = this.userDao.findByUsername(username);
        return user;
    }


}
