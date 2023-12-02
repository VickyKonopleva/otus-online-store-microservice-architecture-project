package com.konopleva.crudeapp.config;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ConfigurationProperties(prefix = "kafka")
public class KafkaConfigurationProperties {
    private List<String> bootstrapServers;
    private Consumer consumer = new Consumer();
    private Producer producer = new Producer();

    public Map<String, Object> buildCommonProperties() {
        Properties properties = new Properties();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(this::getBootstrapServers).to(properties.in(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG));
        return properties;
    }

    public Map<String, Object> buildProducerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(producer.buildProps());
        return properties;
    }

    @Getter
    @Setter
    public static class Producer {
        private Class<?> keySerializer = JsonSerializer.class;
        private Class<?> valueSerializer = JsonSerializer.class;

        private final HashMap<String, String> properties = new HashMap<>();

        public Map<String, Object> buildProps() {
            KafkaConfigurationProperties.Properties props = new KafkaConfigurationProperties.Properties();
            PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
            mapper.from(this::getKeySerializer).to(props.in(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG));
            mapper.from(this::getValueSerializer).to(props.in(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG));
            mapper.from("false").to(props.in(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG));
            mapper.from("false").to(props.in(JsonSerializer.ADD_TYPE_INFO_HEADERS));
            return props.with(this.properties);
        }
    }

    public Map<String, Object> buildConsumerProperties() {
        Map<String, Object> properties = buildCommonProperties();
        properties.putAll(consumer.buildProps());
        return properties;
    }

    @Getter
    @Setter
    public static class Consumer {
        private Class<?> keyValueSerializer = ErrorHandlingDeserializer.class;
        private String autoOffsetReset = "latest";
        private final HashMap<String, String> properties = new HashMap<>();

        public Map<String, Object> buildProps() {
            KafkaConfigurationProperties.Properties props = new KafkaConfigurationProperties.Properties();
            PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
            mapper.from(this::getKeyValueSerializer).to(props.in(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG));
            mapper.from(this::getAutoOffsetReset).to(props.in(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG));
            mapper.from(this::getKeyValueSerializer).to(props.in(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG));
            return props.with(this.properties);
        }
    }

    private static class Properties extends HashMap<String, Object> {
        <V> java.util.function.Consumer<V> in(String key) { return (value) -> put(key, value);}

        Properties with(HashMap<String, String> additionalProperties) {
            putAll(additionalProperties);
            return this;
        }
    }


}
