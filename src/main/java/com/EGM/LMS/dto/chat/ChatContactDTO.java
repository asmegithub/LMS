package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatContactDTO {
    private UUID userId;
    private String name;
    private String email;
    private String role; // System role: ADMIN, INSTRUCTOR, STUDENT
    private String profileImage;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String parentRelationship;
    private UUID courseId;
    private String courseTitle;
    private String contactType; // e.g. "PLATFORM_ADMIN", "COURSE_INSTRUCTOR", "ENROLLED_STUDENT"
}
