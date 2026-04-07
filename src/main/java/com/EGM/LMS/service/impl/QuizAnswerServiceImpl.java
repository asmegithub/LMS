package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.QuestionDTO;
import com.EGM.LMS.dto.QuestionOptionDTO;
import com.EGM.LMS.dto.QuizAnswerDTO;
import com.EGM.LMS.dto.QuizAttemptDTO;
import com.EGM.LMS.model.QuizAnswer;
import com.EGM.LMS.repository.QuestionOptionRepository;
import com.EGM.LMS.repository.QuestionRepository;
import com.EGM.LMS.repository.QuizAnswerRepository;
import com.EGM.LMS.repository.QuizAttemptRepository;
import com.EGM.LMS.service.QuizAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizAnswerServiceImpl implements QuizAnswerService {
    private final QuizAnswerRepository quizAnswerRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    @Override
    public QuizAnswerDTO createQuizAnswer(QuizAnswerDTO quizAnswer) {
        return toDto(quizAnswerRepository.save(toEntity(quizAnswer)));
    }

    @Override
    public List<QuizAnswerDTO> getAllQuizAnswers(UUID studentId, UUID attemptId) {
        var answers = attemptId != null
                ? quizAnswerRepository.findAllByAttempt_Id(attemptId)
                : (studentId != null
                        ? quizAnswerRepository.findAllByAttempt_Student_Id(studentId)
                        : quizAnswerRepository.findAll());
        var answerDtos = new java.util.ArrayList<QuizAnswerDTO>();
        for (QuizAnswer answer : answers) {
            answerDtos.add(toDto(answer));
        }
        return answerDtos;
    }

    @Override
    public QuizAnswerDTO getQuizAnswer(UUID quizAnswerId) {
        return toDto(quizAnswerRepository.findById(quizAnswerId).orElseThrow());
    }

    @Override
    public QuizAnswerDTO updateQuizAnswer(UUID quizAnswerId, QuizAnswerDTO quizAnswer) {
        quizAnswerRepository.findById(quizAnswerId).orElseThrow();
        var entity = toEntity(quizAnswer);
        entity.setId(quizAnswerId);
        return toDto(quizAnswerRepository.save(entity));
    }

    @Override
    public void deleteQuizAnswer(UUID quizAnswerId) {
        quizAnswerRepository.deleteById(quizAnswerId);
    }

    private QuizAnswer toEntity(QuizAnswerDTO quizAnswer) {
        var attemptId = quizAnswer.getAttempt() != null ? quizAnswer.getAttempt().getId() : null;
        var questionId = quizAnswer.getQuestion() != null ? quizAnswer.getQuestion().getId() : null;
        var optionId = quizAnswer.getSelectedOption() != null ? quizAnswer.getSelectedOption().getId() : null;
        return QuizAnswer.builder()
                .attempt(attemptId != null ? quizAttemptRepository.findById(attemptId).orElse(null) : null)
                .question(questionId != null ? questionRepository.findById(questionId).orElse(null) : null)
                .selectedOption(optionId != null ? questionOptionRepository.findById(optionId).orElse(null) : null)
                .isCorrect(quizAnswer.isCorrect())
                .pointsEarned(quizAnswer.getPointsEarned())
                .build();
    }

    private QuizAnswerDTO toDto(QuizAnswer quizAnswer) {
        return QuizAnswerDTO.builder()
                .id(quizAnswer.getId())
                .attempt(quizAnswer.getAttempt() != null
                        ? QuizAttemptDTO.builder().id(quizAnswer.getAttempt().getId()).build()
                        : null)
                .question(quizAnswer.getQuestion() != null
                        ? QuestionDTO.builder().id(quizAnswer.getQuestion().getId()).build()
                        : null)
                .selectedOption(quizAnswer.getSelectedOption() != null
                        ? QuestionOptionDTO.builder().id(quizAnswer.getSelectedOption().getId()).build()
                        : null)
                .isCorrect(quizAnswer.isCorrect())
                .pointsEarned(quizAnswer.getPointsEarned())
                .createdAt(quizAnswer.getCreatedAt())
                .updatedAt(quizAnswer.getUpdatedAt())
                .build();
    }
}
