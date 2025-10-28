package com.jlumanog_dev.patchlens_spring_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
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

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    public JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public WebSecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(configurer ->
                configurer.requestMatchers(HttpMethod.POST, "/api/register", "/api/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user").hasRole("USER").anyRequest().authenticated()

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
    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, DelegatingPasswordEncoder delegatingPasswordEncoder){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider(userDetailsService);
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
}
