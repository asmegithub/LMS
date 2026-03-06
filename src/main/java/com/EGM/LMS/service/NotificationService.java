package com.EGM.LMS.service;

import com.EGM.LMS.dto.NotificationDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    NotificationDTO createNotification(NotificationDTO notification);
    /** Create a notification for all admin users (for admin supervision events). */
    void notifyAdmins(String type, String title, String message, String relatedType, String relatedId, String actionUrl);
    List<NotificationDTO> getMyNotifications();
    long getMyUnreadCount();
    List<NotificationDTO> getAllNotifications();
    NotificationDTO getNotification(UUID notificationId);
    NotificationDTO updateNotification(UUID notificationId, NotificationDTO notification);
    void deleteNotification(UUID notificationId);
}
