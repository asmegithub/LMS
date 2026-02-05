package com.EGM.LMS.controller;

import com.EGM.LMS.dto.QuizDTO;
import com.EGM.LMS.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;

    @PostMapping
    ResponseEntity<QuizDTO> createQuiz(@RequestBody QuizDTO quizDto) {
        return ResponseEntity.ok(quizService.createQuiz(quizDto));
    }

    @GetMapping
    ResponseEntity<List<QuizDTO>> getAllQuizzes() {
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{quizId}")
    ResponseEntity<QuizDTO> getQuiz(@PathVariable UUID quizId) {
        return ResponseEntity.ok(quizService.getQuiz(quizId));
    }

    @PutMapping("/{quizId}")
    ResponseEntity<QuizDTO> updateQuiz(@PathVariable UUID quizId, @RequestBody QuizDTO quizDto) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, quizDto));
    }

    @DeleteMapping("/{quizId}")
    ResponseEntity<Void> deleteQuiz(@PathVariable UUID quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
