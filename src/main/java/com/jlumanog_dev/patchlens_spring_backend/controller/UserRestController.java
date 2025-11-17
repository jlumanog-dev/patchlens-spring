package com.jlumanog_dev.patchlens_spring_backend.controller;

import com.jlumanog_dev.patchlens_spring_backend.dto.HeroesPlayedByUserDTO;
import com.jlumanog_dev.patchlens_spring_backend.dto.UserDTO;
import com.jlumanog_dev.patchlens_spring_backend.entity.Hero;
import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import com.jlumanog_dev.patchlens_spring_backend.exception.AuthenticationErrorException;
import com.jlumanog_dev.patchlens_spring_backend.services.HeroService;
import com.jlumanog_dev.patchlens_spring_backend.services.JwtService;
import com.jlumanog_dev.patchlens_spring_backend.services.OpenDotaRestService;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserRestController {
    private UserService userService;
    private BCryptPasswordEncoder passwordEncoder;
    private DelegatingPasswordEncoder delegatingPasswordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private ModelMapper modelMapper;
    private OpenDotaRestService openDotaRestService;
    private HeroService heroService;
    @Autowired
    public UserRestController(HeroService heroService, OpenDotaRestService openDotaRestService, ModelMapper modelMapper, UserService userService, AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder, DelegatingPasswordEncoder delegatingPasswordEncoder, JwtService jwtService){
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.delegatingPasswordEncoder = delegatingPasswordEncoder;
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtService = jwtService;
        this.openDotaRestService = openDotaRestService;
        this.heroService = heroService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User payloadUser){
        String temp = payloadUser.getPassword();
        System.out.println(temp);
        Object encodePassword = this.passwordEncoder.encode(payloadUser.getPassword());
        String finalEncodedValue = "{bcrypt}" + encodePassword;
        payloadUser.setPassword(finalEncodedValue);
        payloadUser.setRole("USER");
        System.out.println(payloadUser.getUsername());
        System.out.println(payloadUser.getPassword());
        System.out.println(payloadUser.getEmail());
        this.userService.save(payloadUser);

        UserDetails user;
        String token;
        Authentication authObject = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(payloadUser.getUsername(), temp));
        user = (UserDetails) authObject.getPrincipal();
        token = jwtService.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("STATUS", HttpStatus.OK);
        response.put("TOKEN", token);
        return ResponseEntity.ok(response);
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
            System.out.println(token);
            response.put("MESSAGE", "NO ERROR IN AUTHENTICATION, VALID LOGIN");
        }catch (Exception e){
            throw new AuthenticationErrorException("Invalid credentials - occurred in /login");
        }
        response.put("TOKEN", token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDTO> getUserData(){
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication(); // retrieve the authenticated user from the SecurityContextHolder.
        UserDetails userDetails = (UserDetails) authUser.getPrincipal(); //map data from authentication object to user details to access username value later
        User user = this.userService.findByUsername(userDetails.getUsername());
        UserDTO userDTO = this.modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/user/heroes")
    public ResponseEntity<HeroesPlayedByUserDTO[]> retrieveHeroesPlayedByUser(){
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authUser.getPrincipal();
        User user = this.userService.findByUsername(userDetails.getUsername());
        HeroesPlayedByUserDTO[] playedByUserDTO = this.openDotaRestService.retrieveHeroesPlayed(user.getSteamId());

        Hero hero1 = this.heroService.retrieveOneHero(76);
        System.out.println("OD: " + hero1.getLocalized_name());
        for(HeroesPlayedByUserDTO element : playedByUserDTO){
            System.out.println("Hero ID: " + element.getHero_id());
            Hero hero = this.heroService.retrieveOneHero(element.getHero_id());
            System.out.println("Hero name: " + hero.getLocalized_name());
            element.setLocalized_name(hero.getLocalized_name());
            element.setImg(hero.getHeroStats().getImg());
            element.setRoles(hero.getRoles());
        }
        return ResponseEntity.ok(playedByUserDTO);
    }
}
