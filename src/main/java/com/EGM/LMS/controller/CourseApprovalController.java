package com.EGM.LMS.controller;

import com.EGM.LMS.dto.CourseApprovalDTO;
import com.EGM.LMS.service.AuditLogService;
import com.EGM.LMS.service.CourseApprovalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/course-approvals")
@PreAuthorize("hasAuthority('courses.approve')")
public class CourseApprovalController {
    private final CourseApprovalService courseApprovalService;
    private final AuditLogService auditLogService;

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
    ResponseEntity<CourseApprovalDTO> updateCourseApproval(
            @PathVariable UUID courseApprovalId,
            @RequestBody CourseApprovalDTO courseApprovalDto,
            HttpServletRequest request) {
        CourseApprovalDTO updated = courseApprovalService.updateCourseApproval(courseApprovalId, courseApprovalDto);
        String status = courseApprovalDto.getStatus();
        String action = "APPROVED".equalsIgnoreCase(status) ? "APPROVE_COURSE"
                : "REJECTED".equalsIgnoreCase(status) ? "REJECT_COURSE" : "UPDATE_COURSE_APPROVAL";
        String targetId = updated.getCourse() != null
                ? updated.getCourse().getId() != null ? updated.getCourse().getId().toString() : null
                : null;
        auditLogService.logAction(action, "COURSE", targetId, "status=" + status, request.getRemoteAddr(),
                request.getHeader("User-Agent"));
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{courseApprovalId}")
    ResponseEntity<Void> deleteCourseApproval(@PathVariable UUID courseApprovalId) {
        courseApprovalService.deleteCourseApproval(courseApprovalId);
        return ResponseEntity.noContent().build();
    }
}
