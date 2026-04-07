package com.EGM.LMS.controller;

import com.EGM.LMS.dto.QuizAttemptDTO;
import com.EGM.LMS.service.QuizAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz-attempts")
public class QuizAttemptController {
    private final QuizAttemptService quizAttemptService;

    @PostMapping
    ResponseEntity<QuizAttemptDTO> createQuizAttempt(@RequestBody QuizAttemptDTO quizAttemptDto) {
        return ResponseEntity.ok(quizAttemptService.createQuizAttempt(quizAttemptDto));
    }

    @GetMapping
    ResponseEntity<List<QuizAttemptDTO>> getAllQuizAttempts(@RequestParam(required = false) UUID studentId,
            @RequestParam(required = false) UUID quizId) {
        return ResponseEntity.ok(quizAttemptService.getAllQuizAttempts(studentId, quizId));
    }

    @GetMapping("/{quizAttemptId}")
    ResponseEntity<QuizAttemptDTO> getQuizAttempt(@PathVariable UUID quizAttemptId) {
        return ResponseEntity.ok(quizAttemptService.getQuizAttempt(quizAttemptId));
    }

    @PutMapping("/{quizAttemptId}")
    ResponseEntity<QuizAttemptDTO> updateQuizAttempt(@PathVariable UUID quizAttemptId,
            @RequestBody QuizAttemptDTO quizAttemptDto) {
        return ResponseEntity.ok(quizAttemptService.updateQuizAttempt(quizAttemptId, quizAttemptDto));
    }

    @DeleteMapping("/{quizAttemptId}")
    ResponseEntity<Void> deleteQuizAttempt(@PathVariable UUID quizAttemptId) {
        quizAttemptService.deleteQuizAttempt(quizAttemptId);
        return ResponseEntity.noContent().build();
    }
}
