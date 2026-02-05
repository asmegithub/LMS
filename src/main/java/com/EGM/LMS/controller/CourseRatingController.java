package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseRatingDTO;
import com.EGM.LMS.service.CourseRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-ratings")
public class CourseRatingController {
    private final CourseRatingService courseRatingService;

    @PostMapping
    ResponseEntity<CourseRatingDTO> createCourseRating(@RequestBody CourseRatingDTO courseRatingDto) {
        return ResponseEntity.ok(courseRatingService.createCourseRating(courseRatingDto));
    }

    @GetMapping
    ResponseEntity<List<CourseRatingDTO>> getAllCourseRatings() {
        return ResponseEntity.ok(courseRatingService.getAllCourseRatings());
    }

    @GetMapping("/{courseRatingId}")
    ResponseEntity<CourseRatingDTO> getCourseRating(@PathVariable UUID courseRatingId) {
        return ResponseEntity.ok(courseRatingService.getCourseRating(courseRatingId));
    }

    @PutMapping("/{courseRatingId}")
    ResponseEntity<CourseRatingDTO> updateCourseRating(@PathVariable UUID courseRatingId, @RequestBody CourseRatingDTO courseRatingDto) {
        return ResponseEntity.ok(courseRatingService.updateCourseRating(courseRatingId, courseRatingDto));
    }

    @DeleteMapping("/{courseRatingId}")
    ResponseEntity<Void> deleteCourseRating(@PathVariable UUID courseRatingId) {
        courseRatingService.deleteCourseRating(courseRatingId);
        return ResponseEntity.noContent().build();
    }
}
