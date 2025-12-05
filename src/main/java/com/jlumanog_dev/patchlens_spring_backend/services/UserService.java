package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;

public interface UserService {
    public void save(User users);
    public User findByPin(Object shaPin);
    public User findByPersona(String persona);
}
