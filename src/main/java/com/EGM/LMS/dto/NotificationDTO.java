package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {

    private UUID id;
    private UserDTO user;
    private String type;
    private String title;
    private String titleAm;
    private String titleOm;
    private String titleGz;
    private String message;
    private String messageAm;
    private String messageOm;
    private String messageGz;
    private Boolean isRead;
    private String relatedId;
    private String relatedType;
    private String actionUrl;
    private String metadata;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
