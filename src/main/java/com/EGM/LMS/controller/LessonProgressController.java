package com.EGM.LMS.controller;

import com.EGM.LMS.dto.LessonProgressDTO;
import com.EGM.LMS.service.LessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson-progresses")
public class LessonProgressController {
    private final LessonProgressService lessonProgressService;

    @PostMapping
    ResponseEntity<LessonProgressDTO> createLessonProgress(@RequestBody LessonProgressDTO lessonProgressDto) {
        return ResponseEntity.ok(lessonProgressService.createLessonProgress(lessonProgressDto));
    }

    @GetMapping
    ResponseEntity<List<LessonProgressDTO>> getAllLessonProgresses() {
        return ResponseEntity.ok(lessonProgressService.getAllLessonProgresses());
    }

    @GetMapping("/{lessonProgressId}")
    ResponseEntity<LessonProgressDTO> getLessonProgress(@PathVariable UUID lessonProgressId) {
        return ResponseEntity.ok(lessonProgressService.getLessonProgress(lessonProgressId));
    }

    @PutMapping("/{lessonProgressId}")
    ResponseEntity<LessonProgressDTO> updateLessonProgress(@PathVariable UUID lessonProgressId, @RequestBody LessonProgressDTO lessonProgressDto) {
        return ResponseEntity.ok(lessonProgressService.updateLessonProgress(lessonProgressId, lessonProgressDto));
    }

    @DeleteMapping("/{lessonProgressId}")
    ResponseEntity<Void> deleteLessonProgress(@PathVariable UUID lessonProgressId) {
        lessonProgressService.deleteLessonProgress(lessonProgressId);
        return ResponseEntity.noContent().build();
    }
}
