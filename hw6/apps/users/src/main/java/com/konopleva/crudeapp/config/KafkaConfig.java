package com.konopleva.crudeapp.config;

import com.konopleva.crudeapp.dto.kafka.UserStateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(KafkaConfigurationProperties.class)
@EnableKafka
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, UserStateDto> userSateProducerFactory(KafkaConfigurationProperties kafkaConfigurationProperties) {
        return new DefaultKafkaProducerFactory<>(kafkaConfigurationProperties.buildProducerProperties());
    }

    @Bean
    public KafkaTemplate<String, UserStateDto> userStateKafkaTemplate(
            ProducerFactory<String, UserStateDto> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }
}
