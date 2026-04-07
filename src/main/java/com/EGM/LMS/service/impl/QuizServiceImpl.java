package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.QuizDTO;
import com.EGM.LMS.model.Quiz;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.QuizRepository;
import com.EGM.LMS.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final LessonRepository lessonRepository;

    @Override
    public QuizDTO createQuiz(QuizDTO quiz) {
        return toDto(quizRepository.save(toEntity(quiz)));
    }

    @Override
    public List<QuizDTO> getAllQuizzes(UUID lessonId, UUID courseId) {
        var quizzes = lessonId != null
                ? quizRepository.findAllByLesson_Id(lessonId)
                : (courseId != null
                        ? quizRepository.findAllByLesson_Section_Course_Id(courseId)
                        : quizRepository.findAll());
        var quizDtos = new java.util.ArrayList<QuizDTO>();
        for (Quiz q : quizzes) {
            quizDtos.add(toDto(q));
        }
        return quizDtos;
    }

    @Override
    public QuizDTO getQuiz(UUID quizId) {
        return toDto(quizRepository.findById(quizId).orElseThrow());
    }

    @Override
    public QuizDTO updateQuiz(UUID quizId, QuizDTO quiz) {
        quizRepository.findById(quizId).orElseThrow();
        var entity = toEntity(quiz);
        entity.setId(quizId);
        return toDto(quizRepository.save(entity));
    }

    @Override
    public void deleteQuiz(UUID quizId) {
        quizRepository.deleteById(quizId);
    }

    private Quiz toEntity(QuizDTO quiz) {
        var lessonId = quiz.getLesson() != null ? quiz.getLesson().getId() : null;
        return Quiz.builder()
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .title(quiz.getTitle())
                .titleAm(quiz.getTitleAm())
                .titleOm(quiz.getTitleOm())
                .description(quiz.getDescription())
                .quizType(quiz.getQuizType())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .timeLimit(quiz.getTimeLimit())
                .shuffleQuestions(quiz.isShuffleQuestions())
                .shuffleOptions(quiz.isShuffleOptions())
                .showCorrectAnswers(quiz.getShowCorrectAnswers())
                .isActive(quiz.isActive())
                .build();
    }

    private QuizDTO toDto(Quiz quiz) {
        return QuizDTO.builder()
                .id(quiz.getId())
                .lesson(quiz.getLesson() != null ? LessonDTO.builder().id(quiz.getLesson().getId()).build() : null)
                .title(quiz.getTitle())
                .titleAm(quiz.getTitleAm())
                .titleOm(quiz.getTitleOm())
                .description(quiz.getDescription())
                .quizType(quiz.getQuizType())
                .passingScore(quiz.getPassingScore())
                .maxAttempts(quiz.getMaxAttempts())
                .timeLimit(quiz.getTimeLimit())
                .shuffleQuestions(quiz.isShuffleQuestions())
                .shuffleOptions(quiz.isShuffleOptions())
                .showCorrectAnswers(quiz.getShowCorrectAnswers())
                .isActive(quiz.isActive())
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .build();
    }
}
