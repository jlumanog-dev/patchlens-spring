package com.jlumanog_dev.patchlens_spring_backend.custom_auth;

import com.jlumanog_dev.patchlens_spring_backend.entity.User;
import com.jlumanog_dev.patchlens_spring_backend.exception.AuthenticationErrorException;
import com.jlumanog_dev.patchlens_spring_backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomPinAuthProvider implements AuthenticationProvider {
    public UserService userService;
    public DelegatingPasswordEncoder passwordEncoder;
    public CustomPinAuthProvider(UserService userService){
        this.userService = userService;
    }

    public void setPasswordEncoder(DelegatingPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authObject) throws AuthenticationException {
        String pin = (String) authObject.getPrincipal();
        String shaEncoded = SHAUtility.shaHash(pin);
        List<GrantedAuthority> authorities;
        try{
            User user = this.userService.findByPin(shaEncoded);
            if(this.passwordEncoder.matches(pin, user.getPinField())){
                authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                System.out.println("MATCH!!!");
                return new PinAuthenticationToken(user, authorities);
            }else{
                throw new AuthenticationErrorException("Invalid credentials - occurred in /login");
            }

        }catch (Exception e){
            throw new AuthenticationErrorException("Invalid credentials - occurred in /login");
        }
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return PinAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
