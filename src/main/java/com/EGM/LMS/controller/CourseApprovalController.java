package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseApprovalDTO;
import com.EGM.LMS.service.CourseApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-approvals")
public class CourseApprovalController {
    private final CourseApprovalService courseApprovalService;

    @PostMapping
    ResponseEntity<CourseApprovalDTO> createCourseApproval(@RequestBody CourseApprovalDTO courseApprovalDto) {
        return ResponseEntity.ok(courseApprovalService.createCourseApproval(courseApprovalDto));
    }

    @GetMapping
    ResponseEntity<List<CourseApprovalDTO>> getAllCourseApprovals() {
        return ResponseEntity.ok(courseApprovalService.getAllCourseApprovals());
    }

    @GetMapping("/{courseApprovalId}")
    ResponseEntity<CourseApprovalDTO> getCourseApproval(@PathVariable UUID courseApprovalId) {
        return ResponseEntity.ok(courseApprovalService.getCourseApproval(courseApprovalId));
    }

    @PutMapping("/{courseApprovalId}")
    ResponseEntity<CourseApprovalDTO> updateCourseApproval(@PathVariable UUID courseApprovalId, @RequestBody CourseApprovalDTO courseApprovalDto) {
        return ResponseEntity.ok(courseApprovalService.updateCourseApproval(courseApprovalId, courseApprovalDto));
    }

    @DeleteMapping("/{courseApprovalId}")
    ResponseEntity<Void> deleteCourseApproval(@PathVariable UUID courseApprovalId) {
        courseApprovalService.deleteCourseApproval(courseApprovalId);
        return ResponseEntity.noContent().build();
    }
}
