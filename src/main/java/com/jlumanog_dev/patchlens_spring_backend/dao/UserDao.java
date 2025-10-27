package com.jlumanog_dev.patchlens_spring_backend.dao;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;

public interface UserDao {
    public void save(User users);
    public User findByUsername(Object username);
}
