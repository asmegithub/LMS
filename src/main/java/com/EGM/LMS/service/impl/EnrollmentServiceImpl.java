package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Enrollment;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollment) {
        return toDto(enrollmentRepository.save(toEntity(enrollment)));
    }

    @Override
    public List<EnrollmentDTO> getAllEnrollments() {
        var enrollments = enrollmentRepository.findAll();
        var enrollmentDtos = new java.util.ArrayList<EnrollmentDTO>();
        for (Enrollment enrollment : enrollments) {
            enrollmentDtos.add(toDto(enrollment));
        }
        return enrollmentDtos;
    }

    @Override
    public EnrollmentDTO getEnrollment(UUID enrollmentId) {
        return toDto(enrollmentRepository.findById(enrollmentId).orElseThrow());
    }

    @Override
    public EnrollmentDTO updateEnrollment(UUID enrollmentId, EnrollmentDTO enrollment) {
        enrollmentRepository.findById(enrollmentId).orElseThrow();
        var entity = toEntity(enrollment);
        entity.setId(enrollmentId);
        return toDto(enrollmentRepository.save(entity));
    }

    @Override
    public void deleteEnrollment(UUID enrollmentId) {
        enrollmentRepository.deleteById(enrollmentId);
    }

    private Enrollment toEntity(EnrollmentDTO enrollment) {
        var studentId = enrollment.getStudent() != null ? enrollment.getStudent().getId() : null;
        var courseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        var paymentId = enrollment.getPayment() != null ? enrollment.getPayment().getId() : null;
        return Enrollment.builder()
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .payment(paymentId != null ? paymentRepository.findById(paymentId).orElse(null) : null)
                .progress(enrollment.getProgress())
                .completedLessonsCount(enrollment.getCompletedLessonsCount())
                .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
                .isCompleted(enrollment.isCompleted())
                .completedAt(enrollment.getCompletedAt())
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    private EnrollmentDTO toDto(Enrollment enrollment) {
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .student(enrollment.getStudent() != null ? UserDTO.builder().id(enrollment.getStudent().getId()).build() : null)
                .course(enrollment.getCourse() != null ? CourseDTO.builder().id(enrollment.getCourse().getId()).build() : null)
                .payment(enrollment.getPayment() != null ? PaymentDTO.builder().id(enrollment.getPayment().getId()).build() : null)
                .progress(enrollment.getProgress())
                .completedLessonsCount(enrollment.getCompletedLessonsCount())
                .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
                .isCompleted(enrollment.isCompleted())
                .completedAt(enrollment.getCompletedAt())
                .enrolledAt(enrollment.getEnrolledAt())
                .createdAt(enrollment.getCreatedAt())
                .updatedAt(enrollment.getUpdatedAt())
                .build();
    }
}
