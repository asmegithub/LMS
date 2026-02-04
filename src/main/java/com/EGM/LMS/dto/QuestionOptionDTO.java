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
public class QuestionOptionDTO {

    private UUID id;
    private QuestionDTO question;
    private String optionText;
    private String optionTextAm;
    private String optionTextOm;
    private String optionTextGz;
    private Boolean isCorrect;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
