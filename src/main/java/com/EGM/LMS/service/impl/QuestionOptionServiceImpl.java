package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.QuestionDTO;
import com.EGM.LMS.dto.QuestionOptionDTO;
import com.EGM.LMS.model.QuestionOption;
import com.EGM.LMS.repository.QuestionOptionRepository;
import com.EGM.LMS.repository.QuestionRepository;
import com.EGM.LMS.repository.QuizRepository;
import com.EGM.LMS.service.QuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionOptionServiceImpl implements QuestionOptionService {
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Override
    public QuestionOptionDTO createQuestionOption(QuestionOptionDTO questionOption) {
        return toDto(questionOptionRepository.save(toEntity(questionOption)));
    }

    @Override
    public List<QuestionOptionDTO> getAllQuestionOptions(UUID questionId, UUID quizId, UUID courseId) {
        var options = questionId != null
                ? questionOptionRepository.findAllByQuestion_Id(questionId)
                : (quizId != null
                        ? findOptionsByQuizId(quizId)
                        : (courseId != null
                                ? findOptionsByCourseId(courseId)
                                : questionOptionRepository.findAll()));
        var optionDtos = new java.util.ArrayList<QuestionOptionDTO>();
        for (QuestionOption option : options) {
            optionDtos.add(toDto(option));
        }
        return optionDtos;
    }

    @Override
    public QuestionOptionDTO getQuestionOption(UUID questionOptionId) {
        return toDto(questionOptionRepository.findById(questionOptionId).orElseThrow());
    }

    @Override
    public QuestionOptionDTO updateQuestionOption(UUID questionOptionId, QuestionOptionDTO questionOption) {
        questionOptionRepository.findById(questionOptionId).orElseThrow();
        var entity = toEntity(questionOption);
        entity.setId(questionOptionId);
        return toDto(questionOptionRepository.save(entity));
    }

    @Override
    public void deleteQuestionOption(UUID questionOptionId) {
        questionOptionRepository.deleteById(questionOptionId);
    }

    private QuestionOption toEntity(QuestionOptionDTO questionOption) {
        var questionId = questionOption.getQuestion() != null ? questionOption.getQuestion().getId() : null;
        return QuestionOption.builder()
                .question(questionId != null ? questionRepository.findById(questionId).orElse(null) : null)
                .optionText(questionOption.getOptionText())
                .optionTextAm(questionOption.getOptionTextAm())
                .optionTextOm(questionOption.getOptionTextOm())
                .optionTextGz(questionOption.getOptionTextGz())
                .isCorrect(questionOption.getIsCorrect())
                .orderIndex(questionOption.getOrderIndex())
                .build();
    }

    private List<QuestionOption> findOptionsByQuizId(UUID quizId) {
        var questions = questionRepository.findAllByQuiz_Id(quizId);
        if (questions.isEmpty())
            return java.util.List.of();
        var questionIds = questions.stream().map(q -> q.getId()).toList();
        return questionOptionRepository.findAllByQuestion_IdIn(questionIds);
    }

    private List<QuestionOption> findOptionsByCourseId(UUID courseId) {
        var quizzes = quizRepository.findAllByLesson_Section_Course_Id(courseId);
        if (quizzes.isEmpty())
            return java.util.List.of();
        var quizIds = quizzes.stream().map(q -> q.getId()).toList();
        var questions = questionRepository.findAllByQuiz_IdIn(quizIds);
        if (questions.isEmpty())
            return java.util.List.of();
        var questionIds = questions.stream().map(q -> q.getId()).toList();
        return questionOptionRepository.findAllByQuestion_IdIn(questionIds);
    }

    private QuestionOptionDTO toDto(QuestionOption questionOption) {
        return QuestionOptionDTO.builder()
                .id(questionOption.getId())
                .question(questionOption.getQuestion() != null
                        ? QuestionDTO.builder().id(questionOption.getQuestion().getId()).build()
                        : null)
                .optionText(questionOption.getOptionText())
                .optionTextAm(questionOption.getOptionTextAm())
                .optionTextOm(questionOption.getOptionTextOm())
                .optionTextGz(questionOption.getOptionTextGz())
                .isCorrect(questionOption.getIsCorrect())
                .orderIndex(questionOption.getOrderIndex())
                .createdAt(questionOption.getCreatedAt())
                .updatedAt(questionOption.getUpdatedAt())
                .build();
    }
}
