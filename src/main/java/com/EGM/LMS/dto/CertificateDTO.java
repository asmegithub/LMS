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
public class CertificateDTO {

    private UUID id;
    private EnrollmentDTO enrollment;
    private UserDTO student;
    private CourseDTO course;
    private CertificateTemplateDTO template;
    private String certificateNumber;
    private String certificateUrl;
    private String verificationCode;
    private LocalDateTime issuedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
