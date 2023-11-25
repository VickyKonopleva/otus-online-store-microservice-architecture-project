package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.service.IdempotenceKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders/generateIKey")
@RequiredArgsConstructor
public class IdempotenceKeyController {
    private final IdempotenceKeyService idempotenceKeyService;
    @GetMapping()
    public ResponseEntity<String> generateKey() {
        var key = idempotenceKeyService.generateKey();
        return new ResponseEntity<>(key, HttpStatus.OK);
    }
}
