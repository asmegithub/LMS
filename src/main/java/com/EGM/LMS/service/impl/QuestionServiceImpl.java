package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.QuestionDTO;
import com.EGM.LMS.dto.QuizDTO;
import com.EGM.LMS.model.Question;
import com.EGM.LMS.repository.QuestionRepository;
import com.EGM.LMS.repository.QuizRepository;
import com.EGM.LMS.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    public QuestionDTO createQuestion(QuestionDTO question) {
        return toDto(questionRepository.save(toEntity(question)));
    }

    @Override
    public List<QuestionDTO> getAllQuestions(UUID quizId, UUID courseId) {
        var questions = quizId != null
                ? questionRepository.findAllByQuiz_Id(quizId)
                : (courseId != null
                        ? findQuestionsByCourseId(courseId)
                        : questionRepository.findAll());
        var questionDtos = new java.util.ArrayList<QuestionDTO>();
        for (Question question : questions) {
            questionDtos.add(toDto(question));
        }
        return questionDtos;
    }

    @Override
    public QuestionDTO getQuestion(UUID questionId) {
        return toDto(questionRepository.findById(questionId).orElseThrow());
    }

    @Override
    public QuestionDTO updateQuestion(UUID questionId, QuestionDTO question) {
        questionRepository.findById(questionId).orElseThrow();
        var entity = toEntity(question);
        entity.setId(questionId);
        return toDto(questionRepository.save(entity));
    }

    @Override
    public void deleteQuestion(UUID questionId) {
        questionRepository.deleteById(questionId);
    }

    private Question toEntity(QuestionDTO question) {
        var quizId = question.getQuiz() != null ? question.getQuiz().getId() : null;
        return Question.builder()
                .quiz(quizId != null ? quizRepository.findById(quizId).orElse(null) : null)
                .questionText(question.getQuestionText())
                .questionTextAm(question.getQuestionTextAm())
                .questionTextOm(question.getQuestionTextOm())
                .questionTextGz(question.getQuestionTextGz())
                .type(question.getType())
                .explanation(question.getExplanation())
                .explanationAm(question.getExplanationAm())
                .explanationOm(question.getExplanationOm())
                .explanationGz(question.getExplanationGz())
                .points(question.getPoints())
                .orderIndex(question.getOrderIndex())
                .imageUrl(question.getImageUrl())
                .build();
    }

    private List<Question> findQuestionsByCourseId(UUID courseId) {
        var quizzes = quizRepository.findAllByLesson_Section_Course_Id(courseId);
        if (quizzes.isEmpty())
            return java.util.List.of();
        var quizIds = quizzes.stream().map(q -> q.getId()).toList();
        return questionRepository.findAllByQuiz_IdIn(quizIds);
    }

    private QuestionDTO toDto(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .quiz(question.getQuiz() != null ? QuizDTO.builder().id(question.getQuiz().getId()).build() : null)
                .questionText(question.getQuestionText())
                .questionTextAm(question.getQuestionTextAm())
                .questionTextOm(question.getQuestionTextOm())
                .questionTextGz(question.getQuestionTextGz())
                .type(question.getType())
                .explanation(question.getExplanation())
                .explanationAm(question.getExplanationAm())
                .explanationOm(question.getExplanationOm())
                .explanationGz(question.getExplanationGz())
                .points(question.getPoints())
                .orderIndex(question.getOrderIndex())
                .imageUrl(question.getImageUrl())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
