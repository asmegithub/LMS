package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseOutcomeDTO;
import com.EGM.LMS.service.CourseOutcomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-outcomes")
public class CourseOutcomeController {
    private final CourseOutcomeService courseOutcomeService;

    @PostMapping
    ResponseEntity<CourseOutcomeDTO> createCourseOutcome(@RequestBody CourseOutcomeDTO courseOutcomeDto) {
        return ResponseEntity.ok(courseOutcomeService.createCourseOutcome(courseOutcomeDto));
    }

    @GetMapping
    ResponseEntity<List<CourseOutcomeDTO>> getAllCourseOutcomes() {
        return ResponseEntity.ok(courseOutcomeService.getAllCourseOutcomes());
    }

    @GetMapping("/{courseOutcomeId}")
    ResponseEntity<CourseOutcomeDTO> getCourseOutcome(@PathVariable UUID courseOutcomeId) {
        return ResponseEntity.ok(courseOutcomeService.getCourseOutcome(courseOutcomeId));
    }

    @PutMapping("/{courseOutcomeId}")
    ResponseEntity<CourseOutcomeDTO> updateCourseOutcome(@PathVariable UUID courseOutcomeId, @RequestBody CourseOutcomeDTO courseOutcomeDto) {
        return ResponseEntity.ok(courseOutcomeService.updateCourseOutcome(courseOutcomeId, courseOutcomeDto));
    }

    @DeleteMapping("/{courseOutcomeId}")
    ResponseEntity<Void> deleteCourseOutcome(@PathVariable UUID courseOutcomeId) {
        courseOutcomeService.deleteCourseOutcome(courseOutcomeId);
        return ResponseEntity.noContent().build();
    }
}
