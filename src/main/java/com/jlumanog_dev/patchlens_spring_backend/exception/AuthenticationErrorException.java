package com.jlumanog_dev.patchlens_spring_backend.exception;

public class AuthenticationErrorException extends RuntimeException{
    public AuthenticationErrorException(String message){
        super(message);
    }
}
