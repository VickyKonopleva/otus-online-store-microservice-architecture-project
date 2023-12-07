package com.konopleva.crudeapp.kafka.consumer;

import com.konopleva.crudeapp.service.UserService;
import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.hibernate.service.spi.ServiceException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserCommandConsumer {
    private final UserService userService;

    public static final String ERROR_MESSAGE ="Invalid message";

    @KafkaListener(groupId = "${kafka.consumer.group-id}",
            topics = "${kafka.topics.user-state}",
            containerFactory = "kafkaListenerContainerFactory",
            batch = "false")
    public void consume(ConsumerRecord<String, String> consumerRecord) throws ValidationException {
        if (consumerRecord != null && !consumerRecord.value().isEmpty()) {
            try {
                userService.processKafkaCommand(consumerRecord.value());
            } catch (Exception e) {
                log.error(e.getClass().getCanonicalName() + ": " + e.getMessage());
            }
        } else {
            throw new ServiceException(ERROR_MESSAGE);
        }

    }
}
