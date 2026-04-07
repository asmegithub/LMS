package com.EGM.LMS.controller;

import com.EGM.LMS.dto.QuizAnswerDTO;
import com.EGM.LMS.service.QuizAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz-answers")
public class QuizAnswerController {
    private final QuizAnswerService quizAnswerService;

    @PostMapping
    ResponseEntity<QuizAnswerDTO> createQuizAnswer(@RequestBody QuizAnswerDTO quizAnswerDto) {
        return ResponseEntity.ok(quizAnswerService.createQuizAnswer(quizAnswerDto));
    }

    @GetMapping
    ResponseEntity<List<QuizAnswerDTO>> getAllQuizAnswers(@RequestParam(required = false) UUID studentId,
                                                          @RequestParam(required = false) UUID attemptId) {
        return ResponseEntity.ok(quizAnswerService.getAllQuizAnswers(studentId, attemptId));
    }

    @GetMapping("/{quizAnswerId}")
    ResponseEntity<QuizAnswerDTO> getQuizAnswer(@PathVariable UUID quizAnswerId) {
        return ResponseEntity.ok(quizAnswerService.getQuizAnswer(quizAnswerId));
    }

    @PutMapping("/{quizAnswerId}")
    ResponseEntity<QuizAnswerDTO> updateQuizAnswer(@PathVariable UUID quizAnswerId, @RequestBody QuizAnswerDTO quizAnswerDto) {
        return ResponseEntity.ok(quizAnswerService.updateQuizAnswer(quizAnswerId, quizAnswerDto));
    }

    @DeleteMapping("/{quizAnswerId}")
    ResponseEntity<Void> deleteQuizAnswer(@PathVariable UUID quizAnswerId) {
        quizAnswerService.deleteQuizAnswer(quizAnswerId);
        return ResponseEntity.noContent().build();
    }
}
