package com.konopleva.crudeapp.adapter;

import com.konopleva.crudeapp.dto.kafka.UserStateDto;
import com.konopleva.crudeapp.entity.User;
import com.konopleva.crudeapp.mapper.KafkaBusinessDataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BusinessDataKafkaAdapter {
    @Value("${kafka.topics.user-state}")
    private String userStateTopic;
    private final KafkaTemplate<String, UserStateDto> kafkaTemplate;

    private final KafkaBusinessDataMapper kafkaBusinessDataMapper;

    @Async
    public void sendUserState(User entity) {
        var command = kafkaBusinessDataMapper.createUserState(entity);
        kafkaTemplate.send(userStateTopic, command);
    }
}
