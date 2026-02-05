package com.EGM.LMS.service;

import com.EGM.LMS.dto.NotificationDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDTO createNotification(NotificationDTO notification);
    List<NotificationDTO> getAllNotifications();
    NotificationDTO getNotification(UUID notificationId);
    NotificationDTO updateNotification(UUID notificationId, NotificationDTO notification);
    void deleteNotification(UUID notificationId);
}
