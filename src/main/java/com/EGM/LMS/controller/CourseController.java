package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    ResponseEntity<List<CourseDTO>> getAllCourses(){
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    @GetMapping("/{courseId}")
    ResponseEntity<CourseDTO> getCourse(@PathVariable UUID courseId){
        return ResponseEntity.ok(courseService.getCourse(courseId));
    }
    @PutMapping("/{courseId}")
    ResponseEntity<CourseDTO> updateCourse(@PathVariable UUID courseId, @RequestBody CourseDTO coursedto){
        return ResponseEntity.ok( courseService.updateCourse( courseId, coursedto ) );
    }
    @DeleteMapping("/{courseId}")
    ResponseEntity<Void> deleteCourse(@PathVariable UUID courseId){
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }
}