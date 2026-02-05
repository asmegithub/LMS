package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseCategoryDTO;
import com.EGM.LMS.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-categories")
public class CourseCategoryController {
    private final CourseCategoryService courseCategoryService;

    @PostMapping
    ResponseEntity<CourseCategoryDTO> createCourseCategory(@RequestBody CourseCategoryDTO courseCategoryDto) {
        return ResponseEntity.ok(courseCategoryService.createCourseCategory(courseCategoryDto));
    }

    @GetMapping
    ResponseEntity<List<CourseCategoryDTO>> getAllCourseCategories() {
        return ResponseEntity.ok(courseCategoryService.getAllCourseCategories());
    }

    @GetMapping("/{courseCategoryId}")
    ResponseEntity<CourseCategoryDTO> getCourseCategory(@PathVariable UUID courseCategoryId) {
        return ResponseEntity.ok(courseCategoryService.getCourseCategory(courseCategoryId));
    }

    @PutMapping("/{courseCategoryId}")
    ResponseEntity<CourseCategoryDTO> updateCourseCategory(@PathVariable UUID courseCategoryId, @RequestBody CourseCategoryDTO courseCategoryDto) {
        return ResponseEntity.ok(courseCategoryService.updateCourseCategory(courseCategoryId, courseCategoryDto));
    }

    @DeleteMapping("/{courseCategoryId}")
    ResponseEntity<Void> deleteCourseCategory(@PathVariable UUID courseCategoryId) {
        courseCategoryService.deleteCourseCategory(courseCategoryId);
        return ResponseEntity.noContent().build();
    }
}
