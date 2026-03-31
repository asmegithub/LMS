package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {
    private final CourseService courseService;

    @PostMapping
    ResponseEntity<CourseDTO> createCourse(@RequestBody CourseDTO coursedto){
        return ResponseEntity.ok(courseService.createCourse(coursedto));
    }
    @GetMapping
    ResponseEntity<List<CourseDTO>> getAllCourses(@RequestParam(required = false) String status){
        if (status == null || status.isBlank()) {
            return ResponseEntity.ok(courseService.getAllCourses());
        }
        return ResponseEntity.ok(courseService.getCoursesByStatus(status));
    }
    @GetMapping("/{courseId}")
    ResponseEntity<CourseDTO> getCourse(@PathVariable UUID courseId){
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }
    @PutMapping("/{courseId}")
    ResponseEntity<CourseDTO> updateCourse(@PathVariable UUID courseId, @RequestBody CourseDTO coursedto){
        return ResponseEntity.ok( courseService.updateCourse( courseId, coursedto ) );
    }

    /** Admin: feature/unfeature a course for the homepage. Body: { "isFeatured": true } */
    @PatchMapping("/{courseId}/featured")
    ResponseEntity<CourseDTO> setFeatured(@PathVariable UUID courseId, @RequestBody Map<String, Object> body){
        boolean isFeatured = false;
        if (body != null && body.get("isFeatured") != null) {
            var v = body.get("isFeatured");
            if (v instanceof Boolean b) isFeatured = b;
            else isFeatured = Boolean.parseBoolean(String.valueOf(v));
        }
        return ResponseEntity.ok(courseService.setFeatured(courseId, isFeatured));
    }

    /** Admin/instructor owner: hide/unhide a course from public listings. Body: { "isPublished": true } */
    @PatchMapping("/{courseId}/visibility")
    ResponseEntity<CourseDTO> setVisibility(@PathVariable UUID courseId, @RequestBody Map<String, Object> body){
        boolean isPublished = false;
        if (body != null && body.get("isPublished") != null) {
            var v = body.get("isPublished");
            if (v instanceof Boolean b) isPublished = b;
            else isPublished = Boolean.parseBoolean(String.valueOf(v));
        }
        return ResponseEntity.ok(courseService.setPublished(courseId, isPublished));
    }
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteCourse(@PathVariable UUID courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}