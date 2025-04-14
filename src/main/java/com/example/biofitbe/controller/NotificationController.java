package com.example.biofitbe.controller;

import com.example.biofitbe.dto.NotificationDTO;
import com.example.biofitbe.dto.NotificationRequest;
import com.example.biofitbe.model.Notification;
import com.example.biofitbe.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(notificationService.getUserNotifications(userId));
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestBody NotificationRequest request,
            @RequestParam String userId) {
        Notification notification = notificationService.createNotification(request, userId);
        return ResponseEntity.ok(NotificationDTO.fromEntity(notification));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/welcome/{userId}")
    public ResponseEntity<NotificationDTO> sendWelcomeNotification(@PathVariable String userId) {
        Notification welcomeNotification = notificationService.createWelcomeNotification(userId);
        return ResponseEntity.ok(NotificationDTO.fromEntity(welcomeNotification));
    }

    @PostMapping("/mark-all-read/{userId}")
    public ResponseEntity<Void> markAllAsRead(@PathVariable String userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-all/{userId}")
    public ResponseEntity<Void> deleteAllNotifications(@PathVariable String userId) {
        notificationService.deleteAllNotifications(userId);
        return ResponseEntity.ok().build();
    }
}
