package com.EGM.LMS.controller;

import com.EGM.LMS.dto.QuestionDTO;
import com.EGM.LMS.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    ResponseEntity<QuestionDTO> createQuestion(@RequestBody QuestionDTO questionDto) {
        return ResponseEntity.ok(questionService.createQuestion(questionDto));
    }

    @GetMapping
    ResponseEntity<List<QuestionDTO>> getAllQuestions() {
        return ResponseEntity.ok(questionService.getAllQuestions());
    }

    @GetMapping("/{questionId}")
    ResponseEntity<QuestionDTO> getQuestion(@PathVariable UUID questionId) {
        return ResponseEntity.ok(questionService.getQuestion(questionId));
    }

    @PutMapping("/{questionId}")
    ResponseEntity<QuestionDTO> updateQuestion(@PathVariable UUID questionId, @RequestBody QuestionDTO questionDto) {
        return ResponseEntity.ok(questionService.updateQuestion(questionId, questionDto));
    }

    @DeleteMapping("/{questionId}")
    ResponseEntity<Void> deleteQuestion(@PathVariable UUID questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
