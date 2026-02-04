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
public class VideoProgressDTO {

    private UUID id;
    private EnrollmentDTO enrollment;
    private LessonDTO lesson;
    private UserDTO student;
    private Integer watchedDuration;
    private Integer totalDuration;
    private BigDecimal watchPercentage;
    private Integer lastWatchedPosition;
    private BigDecimal playbackSpeed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
