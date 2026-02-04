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
public class QuizAnswerDTO {

    private UUID id;
    private QuizAttemptDTO attempt;
    private QuestionDTO question;
    private QuestionOptionDTO selectedOption;
    private boolean isCorrect;
    private int pointsEarned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
