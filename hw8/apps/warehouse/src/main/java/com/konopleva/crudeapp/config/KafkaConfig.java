package com.konopleva.crudeapp.config;

import com.konopleva.crudeapp.dto.kafka.DeliveryState;
import com.konopleva.crudeapp.dto.kafka.OrderState;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
@EnableKafka
public class KafkaConfig {

    @Bean
    public ConsumerFactory<String, OrderState> orderSateConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderState> orderStateKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderState> orderSateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderState> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderSateConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

    @Bean
    public ProducerFactory<String, OrderState> orderSateProducerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigurationProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, OrderState> orderStateKafkaTemplate(
            ProducerFactory<String, OrderState> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ConsumerFactory<String, DeliveryState> deliverySateConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryState> deliveryStateKafkaListenerContainerFactory(
            ConsumerFactory<String, DeliveryState> deliveryStateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryState> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryStateConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

}
