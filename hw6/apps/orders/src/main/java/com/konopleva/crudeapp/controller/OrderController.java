package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/")
    public void createOrder(@RequestBody OrderDto dto) {
        orderService.createOrder(dto);
    }
}
