package com.konopleva.crudeapp.mapper;

import com.konopleva.crudeapp.dto.UserDto;
import com.konopleva.crudeapp.entity.Role;
import com.konopleva.crudeapp.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserDtoMapper {
    public UserDto mapToDto(User user) {
        return (new UserDto())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName())
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setRole(user.getRole().getAuthority());
    }

    public User mapToUser(UserDto dto) {
        var entity = new User();
        entity
                .setEmail(dto.getEmail())
                .setFirstName(dto.getFirstName())
                .setLastName(dto.getLastName())
                .setPassword(dto.getPassword())
                .setRole(Role.ROLE_USER);
        return entity;
    }
}
