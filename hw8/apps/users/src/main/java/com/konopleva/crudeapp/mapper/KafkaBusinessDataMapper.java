package com.konopleva.crudeapp.mapper;

import com.konopleva.crudeapp.dto.kafka.UserStateDto;
import com.konopleva.crudeapp.entity.User;
import org.springframework.stereotype.Component;

@Component
public class KafkaBusinessDataMapper {
    public UserStateDto createUserState(User entity) {
        var kafkaDto = new UserStateDto();
        kafkaDto.setFirstName(entity.getFirstName());
        kafkaDto.setLastName(entity.getLastName());
        kafkaDto.setEmail(entity.getEmail());
        return kafkaDto;
    }
}
