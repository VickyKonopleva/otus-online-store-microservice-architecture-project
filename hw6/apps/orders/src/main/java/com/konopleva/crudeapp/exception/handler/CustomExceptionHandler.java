package com.konopleva.crudeapp.exception.handler;

import com.konopleva.crudeapp.exception.IdempotenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleCustomHttpClientErrorException(HttpClientErrorException ex) {
        String errorMessage = "Custom Error: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleCustomHttpClientErrorException(BadCredentialsException ex) {
        String errorMessage = "Custom Error: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IdempotenceException.class)
    public ResponseEntity<String> handleCustomHttpClientErrorException(IdempotenceException ex) {
        String errorMessage = "Custom Error: " + ex.getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }
}
