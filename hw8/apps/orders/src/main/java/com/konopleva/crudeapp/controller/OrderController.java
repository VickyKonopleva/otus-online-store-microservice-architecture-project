package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.annotation.IdempotenceKey;
import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.entity.Order;
import com.konopleva.crudeapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @IdempotenceKey
    @PostMapping("/")
    public ResponseEntity<String> createOrder(@RequestBody OrderDto dto) {
        var order = orderService.createOrder(dto);
        String message = "Order: '%s' with price %s$ for user %s created at %s";
        return new ResponseEntity<>(
                String.format(
                        message,
                        order.getDescription(),
                        order.getPrice(),
                        order.getAssociatedUserEmail(),
                        order.getCreationTime()),
                HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<List<Order>> getAllUserOrders() {
        var orders = orderService.getAllUserOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }
}
