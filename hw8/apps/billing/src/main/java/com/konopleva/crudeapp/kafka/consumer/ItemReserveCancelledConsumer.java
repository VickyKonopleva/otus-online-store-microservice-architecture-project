package com.konopleva.crudeapp.kafka.consumer;

import com.konopleva.crudeapp.service.BillingService;
import liquibase.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.xml.bind.ValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemReserveCancelledConsumer {
    private final BillingService billingService;

    public static final String ERROR_MESSAGE ="Invalid message";

    @KafkaListener(groupId = "${kafka.consumer.group-id}",
            topics = "${kafka.topics.item-reserve-cancelled}",
            containerFactory = "orderStateFromWarehouseKafkaListenerContainerFactory",
            batch = "false")
    public void consume(ConsumerRecord<String, String> consumerRecord) throws ValidationException {
        if (consumerRecord != null && StringUtil.isNotEmpty(consumerRecord.value())) {
            try {
                billingService.processItemReserveCancelledKafkaMessage(consumerRecord.value());
            } catch (Exception e) {
                log.error(e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
        } else {
            throw new ValidationException(ERROR_MESSAGE);
        }

    }
}
