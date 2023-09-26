package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.entity.Order;
import com.konopleva.crudeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public Order createOrder(OrderDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var order =  new Order()
                .setAssociatedUserEmail(email)
                .setDescription(dto.getDescription())
                .setPrice(dto.getPrice());
        order = orderRepository.save(order);
        log.info("Order with description {} and price {} for user {} created at {}",
                order.getDescription(),
                order.getPrice(),
                order.getAssociatedUserEmail(),
                order.getCreationTime());
        return order;
    }

    public List<Order> getAllUserOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return orderRepository.findAllByUserEmail(email);
    }
}
