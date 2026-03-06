package com.EGM.LMS.controller;

import com.EGM.LMS.dto.NotificationDTO;
import com.EGM.LMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    ResponseEntity<NotificationDTO> createNotification(@RequestBody NotificationDTO notificationDto) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDto));
    }

    @GetMapping
    ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/me")
    ResponseEntity<List<NotificationDTO>> getMyNotifications() {
        return ResponseEntity.ok(notificationService.getMyNotifications());
    }

    @GetMapping("/me/unread-count")
    ResponseEntity<Long> getUnreadCount() {
        return ResponseEntity.ok(notificationService.getMyUnreadCount());
    }

    @GetMapping("/{notificationId}")
    ResponseEntity<NotificationDTO> getNotification(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(notificationService.getNotification(notificationId));
    }

    @PutMapping("/{notificationId}")
    ResponseEntity<NotificationDTO> updateNotification(@PathVariable UUID notificationId, @RequestBody NotificationDTO notificationDto) {
        return ResponseEntity.ok(notificationService.updateNotification(notificationId, notificationDto));
    }

    @DeleteMapping("/{notificationId}")
    ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
