package com.konopleva.crudeapp.controller;

import com.konopleva.crudeapp.entity.Notification;
import com.konopleva.crudeapp.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping()
    public ResponseEntity<List<Notification>> findAllNotifications() {
        var notifications = notificationService.findAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }
}
