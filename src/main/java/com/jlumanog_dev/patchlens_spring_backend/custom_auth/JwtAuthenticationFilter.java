package com.jlumanog_dev.patchlens_spring_backend.custom_auth;

import com.jlumanog_dev.patchlens_spring_backend.dto.UserDTO;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private JwtService jwtService;
    private UserService userService;
    @Autowired
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService){
        this.jwtService = jwtService;
        this.userService = userService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException, java.io.IOException {

        //Need to declare ModelMapper here and not perform DI
        /*
        Error description when trying to inject ModelMapper bean from the WebSecurityConfig:
        -----------------------------------------------------------
        The dependencies of some of the beans in the application context form a cycle:
        Relying upon circular references is discouraged and they are prohibited by default. Update your application to remove the dependency cycle between beans.*/

        ModelMapper modelMapper = new ModelMapper();

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        username = jwtService.extractPersona(jwt);
        System.out.println("PERSONA: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("inside jwtFilter:");
            UserDTO user = modelMapper.map(this.userService.findByPersona(username), UserDTO.class);
            if (jwtService.isTokenValid(jwt, user)) {
                List<GrantedAuthority> authority = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
                PinAuthenticationToken authToken = new PinAuthenticationToken(
                        user, authority);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}