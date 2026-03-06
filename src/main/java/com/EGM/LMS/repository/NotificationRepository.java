package com.EGM.LMS.repository;

import com.EGM.LMS.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUser_IdOrderByCreatedAtDesc(UUID userId);

    long countByUser_IdAndIsReadFalse(UUID userId);
}
