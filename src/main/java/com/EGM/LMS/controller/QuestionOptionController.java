package com.EGM.LMS.controller;

import com.EGM.LMS.dto.QuestionOptionDTO;
import com.EGM.LMS.service.QuestionOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question-options")
public class QuestionOptionController {
    private final QuestionOptionService questionOptionService;

    @PostMapping
    ResponseEntity<QuestionOptionDTO> createQuestionOption(@RequestBody QuestionOptionDTO questionOptionDto) {
        return ResponseEntity.ok(questionOptionService.createQuestionOption(questionOptionDto));
    }

    @GetMapping
    ResponseEntity<List<QuestionOptionDTO>> getAllQuestionOptions() {
        return ResponseEntity.ok(questionOptionService.getAllQuestionOptions());
    }

    @GetMapping("/{questionOptionId}")
    ResponseEntity<QuestionOptionDTO> getQuestionOption(@PathVariable UUID questionOptionId) {
        return ResponseEntity.ok(questionOptionService.getQuestionOption(questionOptionId));
    }

    @PutMapping("/{questionOptionId}")
    ResponseEntity<QuestionOptionDTO> updateQuestionOption(@PathVariable UUID questionOptionId, @RequestBody QuestionOptionDTO questionOptionDto) {
        return ResponseEntity.ok(questionOptionService.updateQuestionOption(questionOptionId, questionOptionDto));
    }

    @DeleteMapping("/{questionOptionId}")
    ResponseEntity<Void> deleteQuestionOption(@PathVariable UUID questionOptionId) {
        questionOptionService.deleteQuestionOption(questionOptionId);
        return ResponseEntity.noContent().build();
    }
}
