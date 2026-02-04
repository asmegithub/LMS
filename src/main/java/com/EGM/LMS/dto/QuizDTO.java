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
public class QuizDTO {

    private UUID id;
    private LessonDTO lesson;
    private String title;
    private String titleAm;
    private String titleOm;
    private String description;
    private String quizType;
    private int passingScore;
    private int maxAttempts;
    private int timeLimit;
    private boolean shuffleQuestions;
    private boolean shuffleOptions;
    private String showCorrectAnswers;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
