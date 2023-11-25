package com.konopleva.crudeapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping()
    public ResponseEntity<String> checkAuth() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
