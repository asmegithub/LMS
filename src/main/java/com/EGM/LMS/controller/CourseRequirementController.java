package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseRequirementDTO;
import com.EGM.LMS.service.CourseRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-requirements")
public class CourseRequirementController {
    private final CourseRequirementService courseRequirementService;

    @PostMapping
    ResponseEntity<CourseRequirementDTO> createCourseRequirement(@RequestBody CourseRequirementDTO courseRequirementDto) {
        return ResponseEntity.ok(courseRequirementService.createCourseRequirement(courseRequirementDto));
    }

    @GetMapping
    ResponseEntity<List<CourseRequirementDTO>> getAllCourseRequirements() {
        return ResponseEntity.ok(courseRequirementService.getAllCourseRequirements());
    }

    @GetMapping("/{courseRequirementId}")
    ResponseEntity<CourseRequirementDTO> getCourseRequirement(@PathVariable UUID courseRequirementId) {
        return ResponseEntity.ok(courseRequirementService.getCourseRequirement(courseRequirementId));
    }

    @PutMapping("/{courseRequirementId}")
    ResponseEntity<CourseRequirementDTO> updateCourseRequirement(@PathVariable UUID courseRequirementId, @RequestBody CourseRequirementDTO courseRequirementDto) {
        return ResponseEntity.ok(courseRequirementService.updateCourseRequirement(courseRequirementId, courseRequirementDto));
    }

    @DeleteMapping("/{courseRequirementId}")
    ResponseEntity<Void> deleteCourseRequirement(@PathVariable UUID courseRequirementId) {
        courseRequirementService.deleteCourseRequirement(courseRequirementId);
        return ResponseEntity.noContent().build();
    }
}
