package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.kafka.DeliveryState;
import com.konopleva.crudeapp.dto.kafka.OrderState;
import com.konopleva.crudeapp.entity.Delivery;
import com.konopleva.crudeapp.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeliveryService {
    @Value("${kafka.topics.order-delivery-scheduled}")
    private String deliveryScheduledTopic;
    @Value("${kafka.topics.order-delivery-completed}")
    private String deliveryCompletedTopic;
    @Value("${kafka.topics.order-delivery-cancel}")
    private String deliveryCancelledTopic;
    private final ObjectMapper objectMapper;
    private final DeliveryRepository deliveryRepository;
    private final KafkaTemplate<String, DeliveryState> kafkaTemplate;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderPayedKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message OrderPayed consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Scheduling delivery...",
                orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        scheduleDelivery(orderState);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processNoItemInStockKafkaMessage(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        log.info("Kafka message No item {} in stock available consumed. Parameters: id = {}, user = {}, price = {}. " +
                        "Cancelling delivery...",
                orderState.getDescription(), orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getPrice());
        cancelDelivery(orderState);
    }

    private void scheduleDelivery(OrderState orderState) {
        var delivery = deliveryRepository.findByOrderId(orderState.getId());
        if (delivery.isEmpty()) {
            var deliveryTime = allocateAvailableTimeSlot();
            if (deliveryTime != null) {
                var entity = new Delivery()
                        .setOrderId(orderState.getId())
                        .setAssociatedUserEmail(orderState.getAssociatedUserEmail())
                        .setAddress(orderState.getAddress())
                        .setPlannedDeliveryTime(deliveryTime)
                        .setStatus("ACTIVE");
                entity = deliveryRepository.save(entity);
                var deliveryState = new DeliveryState()
                        .setId(entity.getId())
                        .setOrderId(entity.getOrderId())
                        .setAssociatedUserEmail(entity.getAssociatedUserEmail())
                        .setAddress(entity.getAddress())
                        .setStatus(entity.getStatus())
                        .setPlannedDeliveryTime(entity.getPlannedDeliveryTime());
                kafkaTemplate.send(deliveryScheduledTopic, deliveryState);
                log.info("Send message to kafka about successful delivery scheduling for order with id {}" +
                        "Delivery scheduled at {}", orderState.getId(), deliveryState.getPlannedDeliveryTime());
            } else {
                var entity = new Delivery()
                        .setOrderId(orderState.getId())
                        .setAssociatedUserEmail(orderState.getAssociatedUserEmail())
                        .setAddress(orderState.getAddress())
                        .setPlannedDeliveryTime(null)
                        .setStatus("NO_AVAILABLE_TIME_SLOTS");
                entity = deliveryRepository.save(entity);
                var deliveryState = new DeliveryState()
                        .setId(entity.getId())
                        .setOrderId(orderState.getId())
                        .setAssociatedUserEmail(orderState.getAssociatedUserEmail())
                        .setAddress(orderState.getAddress())
                        .setStatus("NO_AVAILABLE_TIME_SLOTS")
                        .setPlannedDeliveryTime(null);
                log.info("Send message to kafka about delivery cancel (no available time slots) for order with id {}",
                        orderState.getId());
                kafkaTemplate.send(deliveryCancelledTopic, deliveryState);
            }
        }
    }

    private void cancelDelivery(OrderState orderState) {
        var delivery = deliveryRepository.findByOrderId(orderState.getId());
        if (delivery.isPresent()) {
            var entity = delivery.get();
            entity.setStatus("CANCELLED");
            deliveryRepository.save(entity);
        }
    }

    private LocalDateTime allocateAvailableTimeSlot() {
        var num = deliveryRepository.count();
        if (num == 2) {
            return null;
        } else {
            return LocalDateTime.now().plusDays(1);
        }
    }

    public Delivery confirmDelivery(String orderId) {
        Delivery result = null;
        var delivery = deliveryRepository.findByOrderId(orderId);
        if (delivery.isPresent()) {
            var entity = delivery.get();
            entity.setActualDeliveryTime(LocalDateTime.now());
            entity.setStatus("DONE");
            result = deliveryRepository.save(entity);

            var deliveryState = new DeliveryState()
                    .setId(entity.getId())
                    .setOrderId(entity.getOrderId())
                    .setAssociatedUserEmail(entity.getAssociatedUserEmail())
                    .setAddress(entity.getAddress())
                    .setStatus(entity.getStatus())
                    .setPlannedDeliveryTime(entity.getPlannedDeliveryTime())
                    .setActualDeliveryTime(entity.getActualDeliveryTime());
            kafkaTemplate.send(deliveryCompletedTopic, deliveryState);
            log.info("Send message to kafka about successful delivery for order with id {}" +
                    "Delivery done at {}", deliveryState.getOrderId(), deliveryState.getActualDeliveryTime());
        }
        return result;
    }
}
