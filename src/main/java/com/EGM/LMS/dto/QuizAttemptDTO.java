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
public class QuizAttemptDTO {

    private UUID id;
    private UserDTO student;
    private QuizDTO quiz;
    private BigDecimal score;
    private int totalPoints;
    private int maxPoints;
    private boolean isPassed;
    private int attemptNumber;
    private int timeTaken;
    private LocalDateTime startedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
