package com.EGM.LMS.service;

import com.EGM.LMS.dto.QuizAttemptDTO;

import java.util.List;
import java.util.UUID;

public interface QuizAttemptService {
    QuizAttemptDTO createQuizAttempt(QuizAttemptDTO quizAttempt);

    List<QuizAttemptDTO> getAllQuizAttempts(UUID studentId, UUID quizId);

    QuizAttemptDTO getQuizAttempt(UUID quizAttemptId);

    QuizAttemptDTO updateQuizAttempt(UUID quizAttemptId, QuizAttemptDTO quizAttempt);

    void deleteQuizAttempt(UUID quizAttemptId);
}
