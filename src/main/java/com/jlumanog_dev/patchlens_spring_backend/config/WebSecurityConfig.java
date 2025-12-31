package com.jlumanog_dev.patchlens_spring_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jlumanog_dev.patchlens_spring_backend.custom_auth.CustomPinAuthProvider;
import com.jlumanog_dev.patchlens_spring_backend.custom_auth.JwtAuthenticationFilter;
import com.jlumanog_dev.patchlens_spring_backend.custom_auth.JwtService;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomPinAuthProvider customPinAuthProvider;
    @Autowired
    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, CustomPinAuthProvider customPinAuthProvider){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customPinAuthProvider = customPinAuthProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authenticationProvider(this.customPinAuthProvider).authorizeHttpRequests(configurer ->
                configurer.requestMatchers(HttpMethod.POST, "/api/register", "/api/login").permitAll()
                        .requestMatchers("/api/opendota/**").permitAll()
                        .requestMatchers("/api/user").hasRole("USER")
                        .requestMatchers("/api/heroes/**").hasRole("USER").anyRequest().authenticated()
                         // might change later to only accept authenticated request with ADMIN role

        ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)).addFilterBefore(this.jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.cors(Customizer.withDefaults()); // enable CORS
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    //Define an AuthenticationManager to inject into your rest controller to use authenticate method.
    //Also specify the type of AuthenticationProvider, and injecting UserDetailsService &
    // DelegatingPasswordEncoder spring beans used to for authentication.
    //TAKE NOTE: @Bean annotation also does dependency injection on its parameters/args without @autowired annotation
    @Bean
    public AuthenticationManager authenticationManager(DelegatingPasswordEncoder delegatingPasswordEncoder, CustomPinAuthProvider customPinAuthProvider, UserService userService){
        CustomPinAuthProvider authenticationProvider = new CustomPinAuthProvider(userService);
        authenticationProvider.setPasswordEncoder(delegatingPasswordEncoder);
        return new ProviderManager(authenticationProvider);
    }


/*   use DelegatingPasswordEncoder to authenticate password with {bcrypt} prefix
    because BcryptPasswordEncoder.matches() will not detect the prefix and
    won't match the raw string password with hashed one*/
    @Bean
    public DelegatingPasswordEncoder delegatePasswordEncoder(){
        Map<String, PasswordEncoder> encoder = new HashMap<>();
        encoder.put("noop", NoOpPasswordEncoder.getInstance());
        encoder.put("bcrypt", this.passwordEncoder());
        return new DelegatingPasswordEncoder("bcrypt", encoder);
    }

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    //external dependency used for mapping entity instances to DTOs
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }



}
