package com.konopleva.crudeapp.service;

import com.konopleva.crudeapp.entity.Notification;
import com.konopleva.crudeapp.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findAllNotifications() {
        return notificationRepository.findAll();
    }
}
