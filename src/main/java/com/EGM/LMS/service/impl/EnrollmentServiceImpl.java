package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.InstructorEnrollmentSummaryDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Enrollment;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final PaymentRepository paymentRepository;
    private final InstructorProfileRepository instructorProfileRepository;

    @Override
    @Transactional
    public EnrollmentDTO createEnrollment(EnrollmentDTO enrollment) {
        var student = resolveAuthenticatedUser();
        if (!"STUDENT".equalsIgnoreCase(student.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can enroll in courses");
        }

        var courseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        if (courseId == null) {
            throw new IllegalArgumentException("Course id is required for enrollment");
        }

        var course = courseRepository.findById(courseId).orElseThrow();
        var existingEnrollment = enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), courseId);
        if (existingEnrollment.isPresent()) {
            return toDto(existingEnrollment.get());
        }

        var paymentId = enrollment.getPayment() != null ? enrollment.getPayment().getId() : null;

        var savedEnrollment = enrollmentRepository.save(Enrollment.builder()
                .student(student)
                .course(course)
                .payment(paymentId != null ? paymentRepository.findById(paymentId).orElse(null) : null)
                .progress(enrollment.getProgress() != null ? enrollment.getProgress() : BigDecimal.ZERO)
            .completedLessonsCount(Math.max(0, enrollment.getCompletedLessonsCount() != null ? enrollment.getCompletedLessonsCount() : 0))
                .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
            .isCompleted(Boolean.TRUE.equals(enrollment.getIsCompleted()))
                .completedAt(enrollment.getCompletedAt())
                .enrolledAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt() : LocalDateTime.now())
                .build());

        course.setEnrollmentCount(course.getEnrollmentCount() + 1);
        courseRepository.save(course);

        var instructor = course.getInstructor();
        if (instructor != null) {
            instructor.setTotalStudents(instructor.getTotalStudents() + 1);
            instructorProfileRepository.save(instructor);
        }

        return toDto(savedEnrollment);
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
    public List<EnrollmentDTO> getMyEnrollments() {
        var student = resolveAuthenticatedUser();
        return enrollmentRepository.findAllByStudent_Id(student.getId())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public Optional<EnrollmentDTO> getMyEnrollmentByCourse(UUID courseId) {
        var student = resolveAuthenticatedUser();
        return enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), courseId)
                .map(this::toDto);
    }

        @Override
        public InstructorEnrollmentSummaryDTO getMyInstructorEnrollmentSummary() {
        var instructorUser = resolveAuthenticatedUser();
        var enrollments = enrollmentRepository.findAllByCourse_Instructor_User_Id(instructorUser.getId());

        var totalStudents = enrollments.stream()
            .map(Enrollment::getStudent)
            .filter(student -> student != null && student.getId() != null)
            .map(User::getId)
            .collect(Collectors.toSet())
            .size();

        var totalCourses = enrollments.stream()
            .map(Enrollment::getCourse)
            .filter(course -> course != null && course.getId() != null)
            .map(course -> course.getId())
            .collect(Collectors.toSet())
            .size();

        return InstructorEnrollmentSummaryDTO.builder()
            .totalEnrollments(enrollments.size())
            .totalStudents(totalStudents)
            .totalCourses(totalCourses)
            .build();
        }

    @Override
    @Transactional
    public EnrollmentDTO updateEnrollment(UUID enrollmentId, EnrollmentDTO enrollment) {
        enrollmentRepository.findById(enrollmentId).orElseThrow();
        var entity = toEntity(enrollment);
        entity.setId(enrollmentId);
        return toDto(enrollmentRepository.save(entity));
    }

    @Override
    @Transactional
    public void deleteEnrollment(UUID enrollmentId) {
        var enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        var course = enrollment.getCourse();
        var instructor = course != null ? course.getInstructor() : null;

        enrollmentRepository.deleteById(enrollmentId);

        if (course != null && course.getEnrollmentCount() > 0) {
            course.setEnrollmentCount(course.getEnrollmentCount() - 1);
            courseRepository.save(course);
        }

        if (instructor != null && instructor.getTotalStudents() > 0) {
            instructor.setTotalStudents(instructor.getTotalStudents() - 1);
            instructorProfileRepository.save(instructor);
        }
    }

    private User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new IllegalStateException("Authentication is required");
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
    }

    private Enrollment toEntity(EnrollmentDTO enrollment) {
        var studentId = enrollment.getStudent() != null ? enrollment.getStudent().getId() : null;
        var courseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        var paymentId = enrollment.getPayment() != null ? enrollment.getPayment().getId() : null;
        return Enrollment.builder()
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .payment(paymentId != null ? paymentRepository.findById(paymentId).orElse(null) : null)
                .progress(enrollment.getProgress() != null ? enrollment.getProgress() : BigDecimal.ZERO)
                .completedLessonsCount(Math.max(0, enrollment.getCompletedLessonsCount() != null ? enrollment.getCompletedLessonsCount() : 0))
                .lastAccessedLessonId(enrollment.getLastAccessedLessonId())
                .isCompleted(Boolean.TRUE.equals(enrollment.getIsCompleted()))
                .completedAt(enrollment.getCompletedAt())
                .enrolledAt(enrollment.getEnrolledAt() != null ? enrollment.getEnrolledAt() : LocalDateTime.now())
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
