package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.exception.IdempotenceException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class IdempotenceKeyService {
    public static List<String> IDEMPOTENCE_KEYS = new ArrayList<>();

    public String generateKey() {
        var key = String.valueOf(UUID.randomUUID());
        IDEMPOTENCE_KEYS.add(key);
        return key;
    }

    public boolean checkIdempotenceKey(HttpServletRequest request) {
        String key = request.getHeader("x-request-id");
        if (key == null) {
            throw new IdempotenceException("Idempotence key missing");
        }
        if (!IDEMPOTENCE_KEYS.remove(key)) {
            throw new IdempotenceException("Wrong idempotence key");
        }

        return true;
    }
}
