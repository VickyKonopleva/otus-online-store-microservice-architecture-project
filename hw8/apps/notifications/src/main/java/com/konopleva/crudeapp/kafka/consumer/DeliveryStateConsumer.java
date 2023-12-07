package com.konopleva.crudeapp.kafka.consumer;

import com.konopleva.crudeapp.service.DeliveryServce;
import com.konopleva.crudeapp.service.OrderService;
import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@RequiredArgsConstructor
@Component
public class DeliveryStateConsumer {
    private final DeliveryServce deliveryServce;

    public static final String ERROR_MESSAGE ="Invalid message";

    @KafkaListener(groupId = "${kafka.consumer.group-id}",
            topics = {
                    "${kafka.topics.order-delivery-scheduled}",
                    "${kafka.topics.order-delivery-cancel}"
            },
            containerFactory = "kafkaListenerContainerFactory",
            batch = "false")
    public void consume(ConsumerRecord<String, String> consumerRecord) throws ValidationException {
        if (consumerRecord != null && StringUtil.isNotEmpty(consumerRecord.value())) {
            try {
                deliveryServce.processOrderStateKafkaCommand(consumerRecord.value());
            } catch (Exception e) {
                log.error(e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
        } else {
            throw new ValidationException(ERROR_MESSAGE);
        }

    }
}
