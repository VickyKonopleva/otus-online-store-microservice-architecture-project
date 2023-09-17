package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.dto.UserDto;
import com.konopleva.crudeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final UserService service;

    @PostMapping()
    public void createUser(@RequestBody UserDto dto) {
        service.createUser(dto);
    }
}
