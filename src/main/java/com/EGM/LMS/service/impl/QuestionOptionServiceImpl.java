package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.QuestionDTO;
import com.EGM.LMS.dto.QuestionOptionDTO;
import com.EGM.LMS.model.QuestionOption;
import com.EGM.LMS.repository.QuestionOptionRepository;
import com.EGM.LMS.repository.QuestionRepository;
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

    @Override
    public QuestionOptionDTO createQuestionOption(QuestionOptionDTO questionOption) {
        return toDto(questionOptionRepository.save(toEntity(questionOption)));
    }

    @Override
    public List<QuestionOptionDTO> getAllQuestionOptions() {
        var options = questionOptionRepository.findAll();
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

    private QuestionOptionDTO toDto(QuestionOption questionOption) {
        return QuestionOptionDTO.builder()
                .id(questionOption.getId())
                .question(questionOption.getQuestion() != null ? QuestionDTO.builder().id(questionOption.getQuestion().getId()).build() : null)
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
