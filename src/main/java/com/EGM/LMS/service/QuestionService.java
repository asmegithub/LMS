package com.EGM.LMS.service;

import com.EGM.LMS.dto.QuestionDTO;

import java.util.List;
import java.util.UUID;

public interface QuestionService {
    QuestionDTO createQuestion(QuestionDTO question);
    List<QuestionDTO> getAllQuestions();
    QuestionDTO getQuestion(UUID questionId);
    QuestionDTO updateQuestion(UUID questionId, QuestionDTO question);
    void deleteQuestion(UUID questionId);
}
