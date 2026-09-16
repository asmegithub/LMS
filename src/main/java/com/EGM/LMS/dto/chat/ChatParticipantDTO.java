package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatParticipantDTO {
    private UUID id;
    private UUID userId;
    private String name;
    private String email;
    private String role; // Chat role: ADMIN, MEMBER
    private String userRole; // System role: STUDENT, INSTRUCTOR, ADMIN
    private String profileImage;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String parentRelationship;
    private LocalDateTime joinedAt;
}
