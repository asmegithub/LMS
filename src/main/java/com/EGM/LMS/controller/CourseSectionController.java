package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseSectionDTO;
import com.EGM.LMS.service.CourseSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-sections")
public class CourseSectionController {
    private final CourseSectionService courseSectionService;

    @PostMapping
    ResponseEntity<CourseSectionDTO> createCourseSection(@RequestBody CourseSectionDTO courseSectionDto) {
        return ResponseEntity.ok(courseSectionService.createCourseSection(courseSectionDto));
    }

    @GetMapping
    ResponseEntity<List<CourseSectionDTO>> getAllCourseSections() {
        return ResponseEntity.ok(courseSectionService.getAllCourseSections());
    }

    @GetMapping("/{courseSectionId}")
    ResponseEntity<CourseSectionDTO> getCourseSection(@PathVariable UUID courseSectionId) {
        return ResponseEntity.ok(courseSectionService.getCourseSection(courseSectionId));
    }

    @PutMapping("/{courseSectionId}")
    ResponseEntity<CourseSectionDTO> updateCourseSection(@PathVariable UUID courseSectionId, @RequestBody CourseSectionDTO courseSectionDto) {
        return ResponseEntity.ok(courseSectionService.updateCourseSection(courseSectionId, courseSectionDto));
    }

    @DeleteMapping("/{courseSectionId}")
    ResponseEntity<Void> deleteCourseSection(@PathVariable UUID courseSectionId) {
        courseSectionService.deleteCourseSection(courseSectionId);
        return ResponseEntity.noContent().build();
    }
}
