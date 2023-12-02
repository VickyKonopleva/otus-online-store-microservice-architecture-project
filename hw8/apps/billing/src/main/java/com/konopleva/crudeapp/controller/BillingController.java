package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.dto.ManipulateAccountDto;
import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.entity.AccountBilling;
import com.konopleva.crudeapp.service.BillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingController {
    private final BillingService billingService;

    @PatchMapping("/deposit")
    public void deposit(@RequestBody ManipulateAccountDto dto) {
        billingService.deposit(dto.getValue());
    }

    @PatchMapping("/withdraw")
    public void withdraw(@RequestBody ManipulateAccountDto dto) {
        billingService.withdraw(dto.getValue());
    }

    @GetMapping("/account")
    public ResponseEntity<AccountBilling> getAccount() {
        var account = billingService.getAccount();
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

}
