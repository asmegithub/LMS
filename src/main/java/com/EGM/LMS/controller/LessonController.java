package com.EGM.LMS.controller;

import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;

    @PostMapping
    ResponseEntity<LessonDTO> createLesson(@RequestBody LessonDTO lessonDto) {
        return ResponseEntity.ok(lessonService.createLesson(lessonDto));
    }

    @GetMapping
    ResponseEntity<List<LessonDTO>> getLessons(@RequestParam(required = false) UUID courseId) {
        if (courseId != null) {
            return ResponseEntity.ok(lessonService.getLessonsByCourseId(courseId));
        }
        return ResponseEntity.ok(lessonService.getAllLessons());
    }

    @GetMapping("/{lessonId}")
    ResponseEntity<LessonDTO> getLesson(@PathVariable UUID lessonId) {
        return ResponseEntity.ok(lessonService.getLesson(lessonId));
    }

    @PutMapping("/{lessonId}")
    ResponseEntity<LessonDTO> updateLesson(@PathVariable UUID lessonId, @RequestBody LessonDTO lessonDto) {
        return ResponseEntity.ok(lessonService.updateLesson(lessonId, lessonDto));
    }

    @DeleteMapping("/{lessonId}")
    ResponseEntity<Void> deleteLesson(@PathVariable UUID lessonId) {
        lessonService.deleteLesson(lessonId);
        return ResponseEntity.noContent().build();
    }
}
