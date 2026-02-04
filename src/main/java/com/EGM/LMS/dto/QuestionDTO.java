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
public class QuestionDTO {

    private UUID id;
    private QuizDTO quiz;
    private String questionText;
    private String questionTextAm;
    private String questionTextOm;
    private String questionTextGz;
    private String type;
    private String explanation;
    private String explanationAm;
    private String explanationOm;
    private String explanationGz;
    private int points;
    private int orderIndex;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
