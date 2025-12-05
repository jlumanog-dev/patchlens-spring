package com.jlumanog_dev.patchlens_spring_backend.custom_auth;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class PinAuthenticationToken extends AbstractAuthenticationToken {
    private Object pinPrincipal;
    //used in login where user is not yet authenticated
    public PinAuthenticationToken(Object pin){
        super(null);
        this.pinPrincipal = pin;
        setAuthenticated(false);
    }

    //this constructor is used after user is authenticated via CustomPinAuthProvider
    public PinAuthenticationToken(Object pin, Collection<? extends GrantedAuthority> authority){
        super(authority);
        this.pinPrincipal = pin;
        setAuthenticated(true);
    }

    //must make this to override all methods from AbstractAuthenticationToken interface
    //but not needed for this project
    @Override
    public Object getCredentials() {
        return null; // no password
    }

    @Override
    public Object getPrincipal() {
        return this.pinPrincipal;
    }
}
