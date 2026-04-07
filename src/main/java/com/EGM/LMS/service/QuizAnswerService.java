package com.EGM.LMS.service;

import com.EGM.LMS.dto.QuizAnswerDTO;

import java.util.List;
import java.util.UUID;

public interface QuizAnswerService {
    QuizAnswerDTO createQuizAnswer(QuizAnswerDTO quizAnswer);
    List<QuizAnswerDTO> getAllQuizAnswers(UUID studentId, UUID attemptId);
    QuizAnswerDTO getQuizAnswer(UUID quizAnswerId);
    QuizAnswerDTO updateQuizAnswer(UUID quizAnswerId, QuizAnswerDTO quizAnswer);
    void deleteQuizAnswer(UUID quizAnswerId);
}
