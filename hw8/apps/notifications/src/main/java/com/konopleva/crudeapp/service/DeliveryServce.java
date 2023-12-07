package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.kafka.DeliveryState;
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
public class DeliveryServce {
    private final ObjectMapper objectMapper;

    private final NotificationRepository notificationRepository;
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderStateKafkaCommand(String value) throws JsonProcessingException {
        var deliveryState = objectMapper.readValue(value, DeliveryState.class);
        log.info("Kafka message DeliveryState consumed. Parameters: id = {}, status = {}, orderId = {}, user = {}, planned delivery time = {}. " +
                        "Processing message...",
                deliveryState.getId(), deliveryState.getStatus(), deliveryState.getOrderId(), deliveryState.getAssociatedUserEmail(), deliveryState.getPlannedDeliveryTime());
        processMessage(deliveryState);
    }

    private void processMessage(DeliveryState deliveryState) {
        var notification = new Notification();
        if (deliveryState.getStatus().equals("ACTIVE")) {
            sendDeliveryScheduledNotification(notification, deliveryState);
        } else if (deliveryState.getStatus().equals("CANCELLED")){
            sendDeliveryCancelledNotification(notification, deliveryState);
        }
    }

    private void sendDeliveryCancelledNotification(Notification notification, DeliveryState deliveryState) {
        notification.setDescription("Sent notification about delivery cancelled for orderId= "
                + deliveryState.getOrderId() + " for user " + deliveryState.getAssociatedUserEmail() + ". Delivery time: null");
        notificationRepository.save(notification);
    }

    private void sendDeliveryScheduledNotification(Notification notification, DeliveryState deliveryState) {
        notification.setDescription("Sent notification about delivery scheduling for orderId= "
                + deliveryState.getOrderId() + " for user " + deliveryState.getAssociatedUserEmail() + ". Delivery time: " + deliveryState.getPlannedDeliveryTime());
        notificationRepository.save(notification);
    }
}
