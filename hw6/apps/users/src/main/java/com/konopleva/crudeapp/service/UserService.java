package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.dto.UserDto;
import com.konopleva.crudeapp.repository.UserRepository;
import com.konopleva.crudeapp.mapper.UserDtoMapper;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    private final UserDtoMapper userDtoMapper;

    @Transactional(readOnly = true)
    public UserDto getUserById(String id) {
        var user = repository.findById(id);
        if (user.isPresent()) {
            return userDtoMapper.mapToDto(user.get());
        } else {
            log.info("User with email = {} not found", id);
            throw new EntityNotFoundException("User not found");
        }
    }

    @Transactional
    public void createUser(UserDto dto) {
        var user = repository.findById(dto.getEmail());
        user.ifPresentOrElse(u -> {
            log.info("User with email = {} is already exists", u.getEmail());
            throw new EntityExistsException("User already exists");
        }, () -> {
            var entity = userDtoMapper.mapToUser(dto);
            repository.save(entity);
            log.info("User with email = {} created", entity.getEmail());
        });
    }
    @Transactional
    public void deleteUserById(String id) {
        repository.deleteById(id);
        log.info("User with email = {} deleted", id);
    }

    @Transactional
    public void updateUser(String id, UserDto dto) {
        var entity = repository.findById(id);
        entity.ifPresentOrElse(user -> {
            user
                .setFirstName(dto.getFirstName())
                .setPassword(dto.getPassword())
                .setLastName(dto.getLastName());
            repository.save(user);
            log.info("User with email = {} updated", user.getEmail());
            },
                () -> {
                    log.info("User with email = {} not found", id);
                    throw new EntityNotFoundException("User not found");
            }
        );
    }
}
