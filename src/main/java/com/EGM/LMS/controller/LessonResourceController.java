package com.EGM.LMS.controller;

import com.EGM.LMS.dto.LessonResourceDTO;
import com.EGM.LMS.service.LessonResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson-resources")
public class LessonResourceController {
    private final LessonResourceService lessonResourceService;

    @PostMapping
    ResponseEntity<LessonResourceDTO> createLessonResource(@RequestBody LessonResourceDTO lessonResourceDto) {
        return ResponseEntity.ok(lessonResourceService.createLessonResource(lessonResourceDto));
    }

    @GetMapping
    ResponseEntity<List<LessonResourceDTO>> getAllLessonResources(@RequestParam(required = false) UUID lessonId,
                                                                  @RequestParam(required = false) UUID courseId) {
        return ResponseEntity.ok(lessonResourceService.getAllLessonResources(lessonId, courseId));
    }

    @GetMapping("/{lessonResourceId}")
    ResponseEntity<LessonResourceDTO> getLessonResource(@PathVariable UUID lessonResourceId) {
        return ResponseEntity.ok(lessonResourceService.getLessonResource(lessonResourceId));
    }

    @PutMapping("/{lessonResourceId}")
    ResponseEntity<LessonResourceDTO> updateLessonResource(@PathVariable UUID lessonResourceId, @RequestBody LessonResourceDTO lessonResourceDto) {
        return ResponseEntity.ok(lessonResourceService.updateLessonResource(lessonResourceId, lessonResourceDto));
    }

    @DeleteMapping("/{lessonResourceId}")
    ResponseEntity<Void> deleteLessonResource(@PathVariable UUID lessonResourceId) {
        lessonResourceService.deleteLessonResource(lessonResourceId);
        return ResponseEntity.noContent().build();
    }
}
