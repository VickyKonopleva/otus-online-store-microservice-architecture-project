package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.kafka.OrderStateDto;
import com.konopleva.crudeapp.entity.Notification;
import com.konopleva.crudeapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final ObjectMapper objectMapper;

    private final NotificationRepository notificationRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderStateFromWarehouseKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderStateDto.class);
        log.info("Kafka message No items in warehouse from warehouse service consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Processing message...",
                orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        sendNoItemsNotification(orderState);
    }

    private void sendNoItemsNotification(OrderStateDto orderState) {
        var notification = new Notification();
        notification.setDescription("Sent notification about no items in warehouse for orderId= "
                + orderState.getId() + " for user " + orderState.getAssociatedUserEmail() + ". Description: " + orderState.getDescription());
        notificationRepository.save(notification);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderStateKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderStateDto.class);
        log.info("Kafka message OrderState consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Processing message...",
                orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        processMessage(orderState);
    }

    private void processMessage(OrderStateDto orderState) {
        var notification = new Notification();
        if (orderState.getStatus().equals("PAYED")) {
            sendOrderPayedNotification(notification, orderState);
        } else if (orderState.getStatus().equals("CANCELLED")){
            sendOrderPaymentDeclinedNotification(notification, orderState);
        } else if (orderState.getStatus().equals("PENDING")){
            sendOrderCreatedNotification(notification, orderState);
        }
    }

    private void sendOrderCreatedNotification(Notification notification, OrderStateDto orderState) {
        notification.setDescription("Sent notification about order creation for orderId= "
                + orderState.getId() + " for user " + orderState.getAssociatedUserEmail() + ". Description: " + orderState.getDescription());
        notificationRepository.save(notification);
    }

    private void sendOrderPaymentDeclinedNotification(Notification notification, OrderStateDto orderState) {
        notification.setDescription("Sent notification about order payment cancelled for orderId= "
                + orderState.getId() + " for user " + orderState.getAssociatedUserEmail() + ". Description: " + orderState.getDescription());
        notificationRepository.save(notification);
    }

    private void sendOrderPayedNotification(Notification notification, OrderStateDto orderState) {
        notification.setDescription("Sent notification about successful order payment for orderId= "
        + orderState.getId() + " for user " + orderState.getAssociatedUserEmail() + ". Description: " + orderState.getDescription());
        notificationRepository.save(notification);
    }
}
