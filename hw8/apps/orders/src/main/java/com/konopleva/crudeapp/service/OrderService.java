package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.DeliveryState;
import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.dto.OrderState;
import com.konopleva.crudeapp.entity.Order;
import com.konopleva.crudeapp.entity.OrderStatus;
import com.konopleva.crudeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    @Value("${kafka.topics.order-state}")
    private String orderStateTopic;
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, OrderState> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public Order createOrder(OrderDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        var order =  new Order()
                .setAssociatedUserEmail(email)
                .setDescription(dto.getDescription())
                .setAddress(dto.getAddress())
                .setStatus(OrderStatus.PENDING)
                .setPrice(dto.getPrice());
        order = orderRepository.save(order);
        log.info("Order with description {} and price {} for user {} start processing at {}",
                order.getDescription(),
                order.getPrice(),
                order.getAssociatedUserEmail(),
                order.getCreationTime());
        var orderState = new OrderState()
                .setId(order.getId())
                .setAssociatedUserEmail(order.getAssociatedUserEmail())
                .setDescription(order.getDescription())
                .setAddress(order.getAddress())
                .setPrice(order.getPrice())
                .setStatus(order.getStatus().toString())
                .setCreationTime(order.getCreationTime());
        kafkaTemplate.send(orderStateTopic, orderState);
        log.info("Send message to kafka about order with id {}", order.getId());
        return order;
    }

    public List<Order> getAllUserOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return orderRepository.findAllByUserEmail(email);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderStateFromBillingKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message OrderState from Billing Service consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Updating order payment status...",
                orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        changePaymentStatus(orderState);
    }

    private void changePaymentStatus(OrderState orderState) {
        var order = orderRepository.findById(orderState.getId());
        if (order.isPresent()) {
            if (!order.get().getStatus().equals(OrderStatus.CANCELLED)) {
                if (orderState.getStatus().equals("PAYED")) {
                    order.get().setStatus(OrderStatus.PAYED);
                } else {
                    order.get().setStatus(OrderStatus.CANCELLED);
                }
                orderRepository.save(order.get());
            }
        }
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processDeliveryStateKafkaMessage(String value) throws JsonProcessingException {
        var deliveryState = objectMapper.readValue(value, DeliveryState.class);
        log.info("Kafka message DeliveryState consumed. Parameters: orderId = {}, user = {}, " +
                        "address = {}, status = {}, planned delivery time = {}. Updating order status...",
                deliveryState.getOrderId(),
                deliveryState.getAssociatedUserEmail(),
                deliveryState.getAddress(),
                deliveryState.getStatus(),
                deliveryState.getPlannedDeliveryTime());
        changeOrderDeliveryStatus(deliveryState);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processItemReserveCancelledKafkaMessage(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message No available item of type {} in stock consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Cancelling order...",
                orderState.getDescription(), orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        cancelOrder(orderState);
    }

    private void cancelOrder(OrderState orderState) {
        var entity = orderRepository.findById(orderState.getId());
        if (entity.isPresent()) {
            var order = entity.get();
            order.setStatus(OrderStatus.CANCELLED);
            log.info("order with id {} cancelled", order.getId());
        }
    }

    private void changeOrderDeliveryStatus(DeliveryState deliveryState) {
        var order = orderRepository.findById(deliveryState.getOrderId());
        if (order.isPresent()) {
            if (!order.get().getStatus().equals(OrderStatus.CANCELLED)) {
                if (deliveryState.getStatus().equals("ACTIVE")) {
                    order.get().setStatus(OrderStatus.DELIVERY);
                } else if (deliveryState.getStatus().equals(OrderStatus.DONE.toString())) {
                    order.get().setStatus(OrderStatus.DONE);
                } else if (deliveryState.getStatus().equals("NO_AVAILABLE_TIME_SLOTS")) {
                    order.get().setStatus(OrderStatus.CANCELLED);
                }
                orderRepository.save(order.get());
            }
        }
    }

}
