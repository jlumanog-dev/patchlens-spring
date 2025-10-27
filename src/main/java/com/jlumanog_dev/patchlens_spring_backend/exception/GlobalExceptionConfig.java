package com.jlumanog_dev.patchlens_spring_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionConfig {

    @ExceptionHandler
    public ResponseEntity<AuthErrorResponse> authExceptionHandler(AuthenticationErrorException authErrorException) {
        AuthErrorResponse error = new AuthErrorResponse();
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.setMessage(authErrorException.getMessage());
        error.setTimestamp(System.currentTimeMillis());

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
