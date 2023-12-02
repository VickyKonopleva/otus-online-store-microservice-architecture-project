package com.konopleva.crudeapp.config;

import com.konopleva.crudeapp.dto.DeliveryState;
import com.konopleva.crudeapp.dto.OrderDto;
import com.konopleva.crudeapp.dto.OrderState;
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
    public ConsumerFactory<String, OrderState> orderSateFromBillingConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderState> orderStateFromBillingKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderState> orderSateFromBillingConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderState> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderSateFromBillingConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, OrderState> orderSateFromWarehouseConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderState> orderStateFromWarehouseKafkaListenerContainerFactory(
            ConsumerFactory<String, OrderState> orderSateFromWarehouseConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, OrderState> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderSateFromWarehouseConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, DeliveryState> deliverySateFromBillingConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeliveryState> deliveryStateFromBillingKafkaListenerContainerFactory(
            ConsumerFactory<String, DeliveryState> deliveryStateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, DeliveryState> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deliveryStateConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

}
