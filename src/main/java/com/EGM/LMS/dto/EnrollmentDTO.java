package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentDTO {

    private UUID id;
    private UserDTO student;
    private CourseDTO course;
    private PaymentDTO payment;
    private BigDecimal progress;
    private Integer completedLessonsCount;
    private String lastAccessedLessonId;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime enrolledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
