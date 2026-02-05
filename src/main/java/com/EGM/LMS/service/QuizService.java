package com.EGM.LMS.service;

import com.EGM.LMS.dto.QuizDTO;

import java.util.List;
import java.util.UUID;

public interface QuizService {
    QuizDTO createQuiz(QuizDTO quiz);
    List<QuizDTO> getAllQuizzes();
    QuizDTO getQuiz(UUID quizId);
    QuizDTO updateQuiz(UUID quizId, QuizDTO quiz);
    void deleteQuiz(UUID quizId);
}
