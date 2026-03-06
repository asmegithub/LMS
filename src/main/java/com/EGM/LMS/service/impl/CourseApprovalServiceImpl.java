package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseApprovalDTO;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.CourseApproval;
import com.EGM.LMS.repository.CourseApprovalRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.CourseApprovalService;
import com.EGM.LMS.service.EmailLogService;
import com.EGM.LMS.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseApprovalServiceImpl implements CourseApprovalService {
    private final CourseApprovalRepository courseApprovalRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EmailLogService emailLogService;
    private final NotificationService notificationService;

    @Override
    public CourseApprovalDTO createCourseApproval(CourseApprovalDTO courseApproval) {
        var entity = toEntity(courseApproval);
        entity = courseApprovalRepository.save(entity);
        var course = entity.getCourse();
        notificationService.notifyAdmins(
                "COURSE_PENDING_APPROVAL",
                "Course pending approval",
                "A course was submitted for review: " + (course != null && course.getTitle() != null ? course.getTitle() : entity.getId().toString()),
                "CourseApproval",
                entity.getId().toString(),
                "/admin/courses"
        );
        return toDto(entity);
    }

    @Override
    public List<CourseApprovalDTO> getAllCourseApprovals() {
        var approvals = courseApprovalRepository.findAll();
        var approvalDtos = new java.util.ArrayList<CourseApprovalDTO>();
        for (CourseApproval approval : approvals) {
            approvalDtos.add(toDto(approval));
        }
        return approvalDtos;
    }

    @Override
    public CourseApprovalDTO getCourseApproval(UUID courseApprovalId) {
        return toDto(courseApprovalRepository.findById(courseApprovalId).orElseThrow());
    }

    @Override
    public CourseApprovalDTO updateCourseApproval(UUID courseApprovalId, CourseApprovalDTO courseApproval) {
        var existing = courseApprovalRepository.findById(courseApprovalId).orElseThrow();
        var entity = toEntity(courseApproval);
        entity.setId(courseApprovalId);
        CourseApprovalDTO saved = toDto(courseApprovalRepository.save(entity));
        String approvalStatus = courseApproval.getStatus();
        if ("APPROVED".equalsIgnoreCase(approvalStatus) || "REJECTED".equalsIgnoreCase(approvalStatus)) {
            var course = existing.getCourse();
            if (course != null && course.getInstructor() != null && course.getInstructor().getUser() != null) {
                var instructor = course.getInstructor().getUser();
                String subject = "APPROVED".equalsIgnoreCase(approvalStatus)
                        ? "Your course has been approved"
                        : "Course review: " + (course.getTitle() != null ? course.getTitle() : "Update");
                emailLogService.recordEmail(
                        instructor.getId(),
                        instructor.getEmail(),
                        subject,
                        "APPROVED".equalsIgnoreCase(approvalStatus) ? "APPROVAL" : "REJECTION",
                        "SENT"
                );
            }
        }
        return saved;
    }

    @Override
    public void deleteCourseApproval(UUID courseApprovalId) {
        courseApprovalRepository.deleteById(courseApprovalId);
    }

    private CourseApproval toEntity(CourseApprovalDTO courseApproval) {
        var courseId = courseApproval.getCourse() != null ? courseApproval.getCourse().getId() : null;
        var reviewerId = courseApproval.getReviewer() != null ? courseApproval.getReviewer().getId() : null;
        return CourseApproval.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .reviewer(reviewerId != null ? userRepository.findById(reviewerId).orElse(null) : null)
                .status(courseApproval.getStatus())
                .submittedAt(courseApproval.getSubmittedAt())
                .reviewedAt(courseApproval.getReviewedAt())
                .rejectionReason(courseApproval.getRejectionReason())
                .notes(courseApproval.getNotes())
                .build();
    }

    private CourseApprovalDTO toDto(CourseApproval courseApproval) {
        return CourseApprovalDTO.builder()
                .id(courseApproval.getId())
                .course(courseApproval.getCourse() != null ? CourseDTO.builder().id(courseApproval.getCourse().getId()).build() : null)
                .reviewer(courseApproval.getReviewer() != null ? UserDTO.builder().id(courseApproval.getReviewer().getId()).build() : null)
                .status(courseApproval.getStatus())
                .submittedAt(courseApproval.getSubmittedAt())
                .reviewedAt(courseApproval.getReviewedAt())
                .rejectionReason(courseApproval.getRejectionReason())
                .notes(courseApproval.getNotes())
                .createdAt(courseApproval.getCreatedAt())
                .updatedAt(courseApproval.getUpdatedAt())
                .build();
    }
}
