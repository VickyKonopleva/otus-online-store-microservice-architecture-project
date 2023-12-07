package com.konopleva.crudeapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.konopleva.crudeapp.dto.kafka.UserStateDto;
import com.konopleva.crudeapp.entity.Notification;
import com.konopleva.crudeapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void processKafkaCommand(String value) throws JsonProcessingException {
        var userStateDto = objectMapper.readValue(value, UserStateDto.class);
        String message = String.format(
                "Send 'user registered' notification to user with name %s %s and email: %s",
                userStateDto.getFirstName(),
                userStateDto.getLastName(),
                userStateDto.getEmail());

        var notification = notificationRepository.findById(userStateDto.getEmail());
        notification.ifPresentOrElse(n -> log.info("Notification '{}' has already been sent", message),
                () -> {
                    var entity = new Notification()
                            .setDescription(message);
                    notificationRepository.save(entity);
                    log.info(message);
                });

    }
}
