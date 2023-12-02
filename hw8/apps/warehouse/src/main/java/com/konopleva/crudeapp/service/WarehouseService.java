package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.kafka.DeliveryState;
import com.konopleva.crudeapp.dto.kafka.OrderState;
import com.konopleva.crudeapp.entity.Item;
import com.konopleva.crudeapp.entity.Order;
import com.konopleva.crudeapp.repository.ItemRepository;
import com.konopleva.crudeapp.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WarehouseService {
    @Value("${kafka.topics.item-reserve-cancelled}")
    private String noItemsAvailableTopic;

    private final ObjectMapper objectMapper;

    private final ItemRepository itemRepository;

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, OrderState> kafkaTemplate;
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processOrderPayedKafkaCommand(String value) throws JsonProcessingException {
        var orderState = objectMapper.readValue(value, OrderState.class);
        var order = orderRepository.findById(orderState.getId());
        if (order.isEmpty()) {
            log.info("Kafka message OrderPayed consumed. Parameters: id = {}, user = {}, description = {}. " +
                            "Reserving items in warehouse...",
                    orderState.getId(), orderState.getAssociatedUserEmail(), orderState.getDescription());
            reserveItems(orderState);
        }
    }
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processDeliveryCancelledKafkaMessage(String value) throws JsonProcessingException {
        var deliveryState = objectMapper.readValue(value, DeliveryState.class);
        var order = orderRepository.findById(deliveryState.getOrderId());
        if (order.isPresent()) {
            log.info("Kafka message DeliveryCancelled consumed. Parameters: orderId = {}, user = {}, " +
                            "address = {}, status = {}, planned delivery time = {}. Returning item to the stock...",
                    deliveryState.getOrderId(),
                    deliveryState.getAssociatedUserEmail(),
                    deliveryState.getAddress(),
                    deliveryState.getStatus(),
                    deliveryState.getPlannedDeliveryTime());
            returnItemToStock(order.get().getItem());
        }
    }

    private void returnItemToStock(String item) {
        var entity = itemRepository.findItemByName(item);
        var amount = entity.get().getAmount();
        entity.get().setAmount(amount + 1);
        log.info("Item {} returned to stock", item);
    }

    private void reserveItems(OrderState orderState) {
        var description = orderState.getDescription();
        Item item = null;
        int amount;
        if (
                description.equals("phone")
                || description.equals("laptop")
                || description.equals("headphones")
        ) {
            var entity = itemRepository.findItemByName(description);
            if (entity.isPresent()) {
                item = entity.get();
            }
        }
        if (item != null) {
            amount = item.getAmount();
            if (amount >= 1) {
                amount = amount - 1;
                item.setAmount(amount);
                itemRepository.save(item);
                log.info("item {} reserved", item.getName());
                var order = new Order()
                        .setId(orderState.getId())
                        .setItem(description);
                orderRepository.save(order);
            } else {
                // send message to Kafka that there is no item available
                log.info("There are no available items of kind {} in the store. " +
                        "Sending message to Kafka", item.getName());
                kafkaTemplate.send(noItemsAvailableTopic, orderState);
            }
        }

    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }
}
