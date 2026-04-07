package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.QuizAttemptDTO;
import com.EGM.LMS.dto.QuizDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.QuizAttempt;
import com.EGM.LMS.repository.QuizAttemptRepository;
import com.EGM.LMS.repository.QuizRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuizAttemptServiceImpl implements QuizAttemptService {
    private final QuizAttemptRepository quizAttemptRepository;
    private final UserRepository userRepository;
    private final QuizRepository quizRepository;

    @Override
    public QuizAttemptDTO createQuizAttempt(QuizAttemptDTO quizAttempt) {
        return toDto(quizAttemptRepository.save(toEntity(quizAttempt)));
    }

    @Override
    public List<QuizAttemptDTO> getAllQuizAttempts(UUID studentId, UUID quizId) {
        var attempts = studentId != null && quizId != null
                ? quizAttemptRepository.findAllByStudent_IdAndQuiz_Id(studentId, quizId)
                : (studentId != null
                    ? quizAttemptRepository.findAllByStudent_Id(studentId)
                    : (quizId != null
                        ? quizAttemptRepository.findAllByQuiz_Id(quizId)
                        : quizAttemptRepository.findAll()));
        var attemptDtos = new java.util.ArrayList<QuizAttemptDTO>();
        for (QuizAttempt attempt : attempts) {
            attemptDtos.add(toDto(attempt));
        }
        return attemptDtos;
    }

    @Override
    public QuizAttemptDTO getQuizAttempt(UUID quizAttemptId) {
        return toDto(quizAttemptRepository.findById(quizAttemptId).orElseThrow());
    }

    @Override
    public QuizAttemptDTO updateQuizAttempt(UUID quizAttemptId, QuizAttemptDTO quizAttempt) {
        quizAttemptRepository.findById(quizAttemptId).orElseThrow();
        var entity = toEntity(quizAttempt);
        entity.setId(quizAttemptId);
        return toDto(quizAttemptRepository.save(entity));
    }

    @Override
    public void deleteQuizAttempt(UUID quizAttemptId) {
        quizAttemptRepository.deleteById(quizAttemptId);
    }

    private QuizAttempt toEntity(QuizAttemptDTO quizAttempt) {
        var studentId = quizAttempt.getStudent() != null ? quizAttempt.getStudent().getId() : null;
        var quizId = quizAttempt.getQuiz() != null ? quizAttempt.getQuiz().getId() : null;
        return QuizAttempt.builder()
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .quiz(quizId != null ? quizRepository.findById(quizId).orElse(null) : null)
                .score(quizAttempt.getScore())
                .totalPoints(quizAttempt.getTotalPoints())
                .maxPoints(quizAttempt.getMaxPoints())
                .isPassed(quizAttempt.isPassed())
                .attemptNumber(quizAttempt.getAttemptNumber())
                .timeTaken(quizAttempt.getTimeTaken())
                .startedAt(quizAttempt.getStartedAt())
                .build();
    }

    private QuizAttemptDTO toDto(QuizAttempt quizAttempt) {
        return QuizAttemptDTO.builder()
                .id(quizAttempt.getId())
                .student(quizAttempt.getStudent() != null ? UserDTO.builder().id(quizAttempt.getStudent().getId()).build() : null)
                .quiz(quizAttempt.getQuiz() != null ? QuizDTO.builder().id(quizAttempt.getQuiz().getId()).build() : null)
                .score(quizAttempt.getScore())
                .totalPoints(quizAttempt.getTotalPoints())
                .maxPoints(quizAttempt.getMaxPoints())
                .isPassed(quizAttempt.isPassed())
                .attemptNumber(quizAttempt.getAttemptNumber())
                .timeTaken(quizAttempt.getTimeTaken())
                .startedAt(quizAttempt.getStartedAt())
                .createdAt(quizAttempt.getCreatedAt())
                .updatedAt(quizAttempt.getUpdatedAt())
                .build();
    }
}
