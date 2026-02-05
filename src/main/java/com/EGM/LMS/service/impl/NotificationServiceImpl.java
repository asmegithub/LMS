package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.NotificationDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Notification;
import com.EGM.LMS.repository.NotificationRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public NotificationDTO createNotification(NotificationDTO notification) {
        return toDto(notificationRepository.save(toEntity(notification)));
    }

    @Override
    public List<NotificationDTO> getAllNotifications() {
        var notifications = notificationRepository.findAll();
        var notificationDtos = new java.util.ArrayList<NotificationDTO>();
        for (Notification notification : notifications) {
            notificationDtos.add(toDto(notification));
        }
        return notificationDtos;
    }

    @Override
    public NotificationDTO getNotification(UUID notificationId) {
        return toDto(notificationRepository.findById(notificationId).orElseThrow());
    }

    @Override
    public NotificationDTO updateNotification(UUID notificationId, NotificationDTO notification) {
        notificationRepository.findById(notificationId).orElseThrow();
        var entity = toEntity(notification);
        entity.setId(notificationId);
        return toDto(notificationRepository.save(entity));
    }

    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    private Notification toEntity(NotificationDTO notification) {
        var userId = notification.getUser() != null ? notification.getUser().getId() : null;
        return Notification.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .type(notification.getType())
                .title(notification.getTitle())
                .titleAm(notification.getTitleAm())
                .titleOm(notification.getTitleOm())
                .titleGz(notification.getTitleGz())
                .message(notification.getMessage())
                .messageAm(notification.getMessageAm())
                .messageOm(notification.getMessageOm())
                .messageGz(notification.getMessageGz())
                .isRead(notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .actionUrl(notification.getActionUrl())
                .metadata(notification.getMetadata())
                .build();
    }

    private NotificationDTO toDto(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .user(notification.getUser() != null ? UserDTO.builder().id(notification.getUser().getId()).build() : null)
                .type(notification.getType())
                .title(notification.getTitle())
                .titleAm(notification.getTitleAm())
                .titleOm(notification.getTitleOm())
                .titleGz(notification.getTitleGz())
                .message(notification.getMessage())
                .messageAm(notification.getMessageAm())
                .messageOm(notification.getMessageOm())
                .messageGz(notification.getMessageGz())
                .isRead(notification.getIsRead())
                .relatedId(notification.getRelatedId())
                .relatedType(notification.getRelatedType())
                .actionUrl(notification.getActionUrl())
                .metadata(notification.getMetadata())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .build();
    }
}
