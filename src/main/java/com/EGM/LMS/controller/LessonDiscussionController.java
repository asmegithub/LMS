package com.EGM.LMS.controller;

import com.EGM.LMS.dto.LessonDiscussionDTO;
import com.EGM.LMS.service.LessonDiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson-discussions")
public class LessonDiscussionController {
    private final LessonDiscussionService lessonDiscussionService;

    @PostMapping
    ResponseEntity<LessonDiscussionDTO> createLessonDiscussion(@RequestBody LessonDiscussionDTO lessonDiscussionDto) {
        return ResponseEntity.ok(lessonDiscussionService.createLessonDiscussion(lessonDiscussionDto));
    }

    @GetMapping
    ResponseEntity<List<LessonDiscussionDTO>> getAllLessonDiscussions() {
        return ResponseEntity.ok(lessonDiscussionService.getAllLessonDiscussions());
    }

    @GetMapping("/{lessonDiscussionId}")
    ResponseEntity<LessonDiscussionDTO> getLessonDiscussion(@PathVariable UUID lessonDiscussionId) {
        return ResponseEntity.ok(lessonDiscussionService.getLessonDiscussion(lessonDiscussionId));
    }

    @PutMapping("/{lessonDiscussionId}")
    ResponseEntity<LessonDiscussionDTO> updateLessonDiscussion(@PathVariable UUID lessonDiscussionId, @RequestBody LessonDiscussionDTO lessonDiscussionDto) {
        return ResponseEntity.ok(lessonDiscussionService.updateLessonDiscussion(lessonDiscussionId, lessonDiscussionDto));
    }

    @DeleteMapping("/{lessonDiscussionId}")
    ResponseEntity<Void> deleteLessonDiscussion(@PathVariable UUID lessonDiscussionId) {
        lessonDiscussionService.deleteLessonDiscussion(lessonDiscussionId);
        return ResponseEntity.noContent().build();
    }
}
