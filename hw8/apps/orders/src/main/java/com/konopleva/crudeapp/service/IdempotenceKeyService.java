package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.exception.IdempotenceException;
import com.konopleva.crudeapp.repository.RedisIdempotenceKeyRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdempotenceKeyService {
//    public static List<String> IDEMPOTENCE_KEYS = new ArrayList<>();
    private final RedisIdempotenceKeyRepository redisRepository;

    public String generateKey() {
        var key = String.valueOf(UUID.randomUUID());
        redisRepository.add(key);
//        IDEMPOTENCE_KEYS.add(key);
        return key;
    }

    public boolean checkIdempotenceKey(HttpServletRequest request) {
        String key = request.getHeader("x-idempotence-key");
        if (key == null) {
            throw new IdempotenceException("Idempotence key missing");
        }
        if (!redisRepository.delete(key)) {
            throw new IdempotenceException("Wrong idempotence key");
        }

        return true;
    }
}
