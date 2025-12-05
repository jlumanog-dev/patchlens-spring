package com.jlumanog_dev.patchlens_spring_backend.services;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import com.jlumanog_dev.patchlens_spring_backend.exception.AuthenticationErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/*
Note: the bean/instance for this is used on JwtAuthenticationFilter to retrieve username and role
as UserDetails type used to compare it to JWT to determine if the token is valid with the correct role.

*/



/*@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    private UserService userService;

    @Autowired
    public CustomUserDetailsServiceImpl(UserService userService){
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = this.userService.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid credentials");
        }
        //creating a collection that contains object to be used for indicating the authenticated user's role
        //List<? extends GrantedAuthority> means a List of any subclass that extends/inherits GrantedAuthority
        List<? extends GrantedAuthority> authority = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authority);
    }
}*/
