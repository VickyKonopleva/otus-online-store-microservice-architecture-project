package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.dto.UserDto;
import com.konopleva.crudeapp.entity.User;
import com.konopleva.crudeapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users/current")
@RequiredArgsConstructor
public class CurrentUserController {
    private final UserService service;

    @GetMapping("/")
    public UserDto getUserById() {
        var user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (new UserDto())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword("secured")
                .setRole(user.getRole().getAuthority());
    }

    @PutMapping("/")
    public void updateUser( @RequestBody UserDto dto) {
        var user =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        service.updateUser(user.getEmail(), dto);
    }
}

