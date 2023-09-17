package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.entity.Order;
import com.konopleva.crudeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void createOrder(OrderDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var order =  new Order()
                .setAssociatedUserEmail(email)
                .setDescription(dto.getDescription());

        orderRepository.save(order);
    }
}
