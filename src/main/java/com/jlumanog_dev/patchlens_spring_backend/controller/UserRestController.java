package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import com.jlumanog_dev.patchlens_spring_backend.exception.AuthenticationErrorException;
import com.jlumanog_dev.patchlens_spring_backend.services.JwtService;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;
    private DelegatingPasswordEncoder delegatingPasswordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserRestController(UserService userService, AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder, DelegatingPasswordEncoder delegatingPasswordEncoder, JwtService jwtService){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
        this.authenticationManager = authenticationManager;

        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody User payloadUser){
        Object encodePassword = this.passwordEncoder.encode(payloadUser.getPassword());
        String finalEncodedValue = "{bcrypt}" + encodePassword;
        payloadUser.setPassword(finalEncodedValue);
        this.userService.save(payloadUser);

        Map<String, Object> response = new HashMap<>();
        response.put("STATUS: ", HttpStatus.OK);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User payloadUser){
        Map<String, Object> response = new HashMap<>();
        UserDetails user;
        String token;
        try{
            /*
            passing an Authentication object type to authenticate()
            The AuthenticationManager will then use an AuthenticationProvider, DelegatePasswordEncoder and
            your CustomUserDetailsService to authenticate behind the scenes
            */
            Authentication authObject = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payloadUser.getUsername(), payloadUser.getPassword()));
            user = (UserDetails) authObject.getPrincipal();
            token = this.jwtService.generateToken(user);
            response.put("MESSAGE", "NO ERROR IN AUTHENTICATION, VALID LOGIN");
        }catch (Exception e){
            throw new AuthenticationErrorException("Invalid credentials - occurred in /login");
        }
        response.put("TOKEN", token);
        return ResponseEntity.ok(response);
    }
}
