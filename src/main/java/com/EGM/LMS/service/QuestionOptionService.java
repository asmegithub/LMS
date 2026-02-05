package com.EGM.LMS.service;

import com.EGM.LMS.dto.QuestionOptionDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionOptionService {
    QuestionOptionDTO createQuestionOption(QuestionOptionDTO questionOption);
    List<QuestionOptionDTO> getAllQuestionOptions();
    QuestionOptionDTO getQuestionOption(UUID questionOptionId);
    QuestionOptionDTO updateQuestionOption(UUID questionOptionId, QuestionOptionDTO questionOption);
    void deleteQuestionOption(UUID questionOptionId);
}
