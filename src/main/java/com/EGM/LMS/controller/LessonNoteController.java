package com.EGM.LMS.controller;

import com.EGM.LMS.dto.LessonNoteDTO;
import com.EGM.LMS.service.LessonNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/lesson-notes")
public class LessonNoteController {
    private final LessonNoteService lessonNoteService;

    @PostMapping
    ResponseEntity<LessonNoteDTO> createLessonNote(@RequestBody LessonNoteDTO lessonNoteDto) {
        return ResponseEntity.ok(lessonNoteService.createLessonNote(lessonNoteDto));
    }

    @GetMapping
    ResponseEntity<List<LessonNoteDTO>> getAllLessonNotes() {
        return ResponseEntity.ok(lessonNoteService.getAllLessonNotes());
    }

    @GetMapping("/{lessonNoteId}")
    ResponseEntity<LessonNoteDTO> getLessonNote(@PathVariable UUID lessonNoteId) {
        return ResponseEntity.ok(lessonNoteService.getLessonNote(lessonNoteId));
    }

    @PutMapping("/{lessonNoteId}")
    ResponseEntity<LessonNoteDTO> updateLessonNote(@PathVariable UUID lessonNoteId, @RequestBody LessonNoteDTO lessonNoteDto) {
        return ResponseEntity.ok(lessonNoteService.updateLessonNote(lessonNoteId, lessonNoteDto));
    }

    @DeleteMapping("/{lessonNoteId}")
    ResponseEntity<Void> deleteLessonNote(@PathVariable UUID lessonNoteId) {
        lessonNoteService.deleteLessonNote(lessonNoteId);
        return ResponseEntity.noContent().build();
    }
}
