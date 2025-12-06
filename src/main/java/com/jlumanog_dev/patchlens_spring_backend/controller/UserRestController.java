package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.jlumanog_dev.patchlens_spring_backend.custom_auth.JwtService;
import com.jlumanog_dev.patchlens_spring_backend.custom_auth.PinAuthenticationToken;
import com.jlumanog_dev.patchlens_spring_backend.custom_auth.SHAUtility;
import com.jlumanog_dev.patchlens_spring_backend.dto.*;
import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import com.jlumanog_dev.patchlens_spring_backend.exception.AuthenticationErrorException;
import com.jlumanog_dev.patchlens_spring_backend.scheduler.HeroStatsScheduler;
import com.jlumanog_dev.patchlens_spring_backend.services.OpenDotaRestService;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;
    private DelegatingPasswordEncoder delegatingPasswordEncoder;
    private ModelMapper modelMapper;
    private HeroStatsScheduler heroStatsScheduler;
    private OpenDotaRestService openDotaRestService;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;

    @Autowired
    public UserRestController( OpenDotaRestService openDotaRestService,
                               ModelMapper modelMapper,
                               UserService userService,
                               BCryptPasswordEncoder passwordEncoder,
                               DelegatingPasswordEncoder delegatingPasswordEncoder,
                               HeroStatsScheduler heroStatsScheduler,
                               AuthenticationManager authenticationManager,
                               JwtService jwtService){
        this.userService = userService;
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
        this.modelMapper = modelMapper;
        this.heroStatsScheduler = heroStatsScheduler;
        this.openDotaRestService = openDotaRestService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User payloadUser){
        String temp = payloadUser.getPinField();

        String usernameGenerate = payloadUser.getPersonaName() + "_" + payloadUser.getPlayerIdField();
        payloadUser.setPersonaName(usernameGenerate);

        Object encodedPin = this.delegatingPasswordEncoder.encode(payloadUser.getPinField());
        String finalEncodedValue =  encodedPin.toString();
        String shaEncoded = SHAUtility.shaHash(payloadUser.getPinField());
        payloadUser.setPinField(finalEncodedValue);
        payloadUser.setShaLookup(shaEncoded);
        payloadUser.setRole("USER");
        this.userService.save(payloadUser);
        Map<String, Object> response = new HashMap<>();

        try{
            System.out.println("AUTHENTICATING");
            Authentication authResult = this.authenticationManager.authenticate(new PinAuthenticationToken(temp));
            UserDTO user = this.modelMapper.map(authResult.getPrincipal(), UserDTO.class);
            System.out.println("PERSONA");
            System.out.println(user.getPersonaName());
            String token = this.jwtService.generateToken(user);
            System.out.println("TOKEN: " + token);
            response.put("TOKEN", token);

            SecurityContextHolder.getContext().setAuthentication(authResult);
            return ResponseEntity.ok(response);

        }catch (Exception e){
            throw new AuthenticationErrorException("error authentication");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User payloadUser){
        Map<String, Object> response = new HashMap<>();
        UserDTO user;
        try{
            Authentication authResult = this.authenticationManager.authenticate(new PinAuthenticationToken(payloadUser.getPinField()));
            user = this.modelMapper.map(authResult.getPrincipal(), UserDTO.class);
            String token = this.jwtService.generateToken(user);
            response.put("TOKEN", token);
            SecurityContextHolder.getContext().setAuthentication(authResult); // must manually set authenticate user to SecurityContextHolder when using custom auth provider
            System.out.println(SecurityContextHolder.getContext().getAuthentication());
            return ResponseEntity.ok(response);
        }catch (Exception e){
            throw new AuthenticationErrorException("Invalid credentials - occurred in /login");
        }

    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData(){
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("principal here - user data");
        System.out.println(authUser.getPrincipal());
        UserDTO user = this.modelMapper.map(authUser.getPrincipal(), UserDTO.class);
        System.out.println(user.getPlayerIdField());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/heroes")
    public ResponseEntity<Map<String, Object>> retrieveHeroesPlayedByUser(){
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("principal here - heroesplayed");
        System.out.println(authUser.getPrincipal());
        UserDTO user = this.modelMapper.map(authUser.getPrincipal(), UserDTO.class);
        System.out.println(user.getPlayerIdField());
        Map<String, Object>playedByUserDTO = this.heroStatsScheduler.heroesPlayedByUser(user.getPlayerIdField());

        return ResponseEntity.ok(playedByUserDTO);
    }

    @GetMapping("/user/recentMatches")
    public ResponseEntity<Map<String, Object>> retrieveRecentMatches(){
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("principal here - recentmatch:");
        System.out.println(authUser.getPrincipal());
        //might add try catch here or some kind of exception handling
        UserDTO user = this.modelMapper.map(authUser.getPrincipal(), UserDTO.class); // seems unnecessary, just making sure I'm using user object with no password field - might change later
        System.out.println(user.getPlayerIdField());
        return ResponseEntity.ok(this.openDotaRestService.retrieveRecentMatches(user.getPlayerIdField()));
    }
}
