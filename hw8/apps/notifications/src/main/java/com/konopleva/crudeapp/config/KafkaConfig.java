package com.konopleva.crudeapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
@EnableKafka
public class KafkaConfig {
    @Bean
    public ConsumerFactory<String, String> userSateConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> userSateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(userSateConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> orderSateConsumerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaConsumerFactory<>(kafkaConfigurationProperties.buildConsumerProperties());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> orderStateKafkaListenerContainerFactory(
            ConsumerFactory<String, String> orderSateConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderSateConsumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }

}
