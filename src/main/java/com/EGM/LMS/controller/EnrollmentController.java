package com.EGM.LMS.controller;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.InstructorEnrollmentSummaryDTO;
import com.EGM.LMS.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final EnrollmentService enrollmentService;

    @PostMapping
    ResponseEntity<EnrollmentDTO> createEnrollment(@RequestBody EnrollmentDTO enrollmentDto) {
        return ResponseEntity.ok(enrollmentService.createEnrollment(enrollmentDto));
    }

    @GetMapping
    ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollments());
    }

    @GetMapping("/me")
    ResponseEntity<?> getMyEnrollments(@RequestParam(required = false) UUID courseId) {
        if (courseId != null) {
            Optional<EnrollmentDTO> enrollment = enrollmentService.getMyEnrollmentByCourse(courseId);
            return enrollment.<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.noContent().build());
        }
        return ResponseEntity.ok(enrollmentService.getMyEnrollments());
    }

    @GetMapping("/me/instructor-summary")
    ResponseEntity<InstructorEnrollmentSummaryDTO> getMyInstructorEnrollmentSummary() {
        return ResponseEntity.ok(enrollmentService.getMyInstructorEnrollmentSummary());
    }

    @GetMapping("/{enrollmentId}")
    ResponseEntity<EnrollmentDTO> getEnrollment(@PathVariable UUID enrollmentId) {
        return ResponseEntity.ok(enrollmentService.getEnrollment(enrollmentId));
    }

    @PutMapping("/{enrollmentId}")
    ResponseEntity<EnrollmentDTO> updateEnrollment(@PathVariable UUID enrollmentId, @RequestBody EnrollmentDTO enrollmentDto) {
        return ResponseEntity.ok(enrollmentService.updateEnrollment(enrollmentId, enrollmentDto));
    }

    @DeleteMapping("/{enrollmentId}")
    ResponseEntity<Void> deleteEnrollment(@PathVariable UUID enrollmentId) {
        enrollmentService.deleteEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }
}
