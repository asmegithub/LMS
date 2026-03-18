package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.InstructorEnrollmentSummaryDTO;
import com.EGM.LMS.dto.PaymentDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Enrollment;
import com.EGM.LMS.model.InstructorEarning;
import com.EGM.LMS.model.OrderItem;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.InstructorEarningRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.repository.OrderItemRepository;
import com.EGM.LMS.repository.PaymentRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.EmailLogService;
import com.EGM.LMS.service.EnrollmentService;
import com.EGM.LMS.service.ReferralBalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final OrderItemRepository orderItemRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private final InstructorEarningRepository instructorEarningRepository;
    private final ReferralBalanceService referralBalanceService;
    private final EmailLogService emailLogService;

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

        var coursePrice = course.getDiscountPrice() != null && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                ? course.getDiscountPrice() : course.getPrice();
        if (coursePrice == null) coursePrice = BigDecimal.ZERO;

        UUID paymentId = enrollment.getPayment() != null ? enrollment.getPayment().getId() : null;
        if (Boolean.TRUE.equals(enrollment.getUseBalance())) {
            var paymentOpt = referralBalanceService.useBalanceForEnrollment(student.getId(), courseId, coursePrice);
            if (paymentOpt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient referral balance for this course");
            }
            paymentId = paymentOpt.get();
        }

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
            creditInstructorEarning(instructor.getId(), coursePrice);
        }

        var referrerId = enrollment.getReferrerId();
        if (referrerId != null && !referrerId.equals(student.getId()) && coursePrice.compareTo(BigDecimal.ZERO) > 0) {
            var referralAmount = coursePrice.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
            referralBalanceService.creditReferrer(referrerId, savedEnrollment.getId(), referralAmount);
        }

        emailLogService.recordEmail(
                student.getId(),
                student.getEmail(),
                "Enrollment confirmation: " + (course.getTitle() != null ? course.getTitle() : "Course"),
                "ENROLLMENT",
                "SENT"
        );
        return toDto(savedEnrollment);
    }

    @Override
    @Transactional
    public EnrollmentDTO createEnrollmentForPayment(UUID paymentId) {
        var payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        if (!"COMPLETED".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Payment is not completed: " + paymentId);
        }
        var student = payment.getStudent();
        var course = payment.getCourse();
        if (student == null || course == null) {
            throw new IllegalStateException("Payment must have student and course");
        }
        var existing = enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), course.getId());
        if (existing.isPresent()) {
            return toDto(existing.get());
        }

        var savedEnrollment = enrollmentRepository.save(Enrollment.builder()
                .student(student)
                .course(course)
                .payment(payment)
                .progress(BigDecimal.ZERO)
                .completedLessonsCount(0)
                .isCompleted(false)
                .enrolledAt(LocalDateTime.now())
                .build());

        course.setEnrollmentCount(course.getEnrollmentCount() + 1);
        courseRepository.save(course);

        var instructor = course.getInstructor();
        if (instructor != null) {
            instructor.setTotalStudents(instructor.getTotalStudents() + 1);
            instructorProfileRepository.save(instructor);
        }

        var coursePrice = course.getDiscountPrice() != null && course.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0
                ? course.getDiscountPrice() : course.getPrice();
        if (coursePrice == null) coursePrice = BigDecimal.ZERO;
        try {
            var referrerIdStr = payment.getReferralCode();
            if (referrerIdStr != null && !referrerIdStr.isBlank() && coursePrice.compareTo(BigDecimal.ZERO) > 0) {
                var referrerId = UUID.fromString(referrerIdStr.trim());
                if (!referrerId.equals(student.getId())) {
                    var referralAmount = coursePrice.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
                    referralBalanceService.creditReferrer(referrerId, savedEnrollment.getId(), referralAmount);
                }
            }
        } catch (Exception e) {
            // ignore invalid referrer id in referralCode
        }
        emailLogService.recordEmail(
                student.getId(),
                student.getEmail(),
                "Enrollment confirmation: " + (course.getTitle() != null ? course.getTitle() : "Course"),
                "ENROLLMENT",
                "SENT"
        );
        return toDto(savedEnrollment);
    }

    @Override
    @Transactional
    public List<EnrollmentDTO> createEnrollmentsForOrder(UUID orderId, UUID paymentId) {
        var payment = paymentRepository.findById(paymentId).orElseThrow(() -> new IllegalArgumentException("Payment not found: " + paymentId));
        if (!"COMPLETED".equalsIgnoreCase(payment.getStatus())) {
            throw new IllegalStateException("Payment is not completed: " + paymentId);
        }
        var order = payment.getOrder();
        if (order == null || !order.getId().equals(orderId)) {
            throw new IllegalArgumentException("Order does not match payment");
        }
        var student = order.getStudent();
        if (student == null) {
            throw new IllegalStateException("Order must have student");
        }
        var items = orderItemRepository.findByOrder_Id(orderId);
        var result = new java.util.ArrayList<EnrollmentDTO>();
        for (OrderItem item : items) {
            var course = item.getCourse();
            if (course == null) continue;
            var existing = enrollmentRepository.findFirstByStudent_IdAndCourse_Id(student.getId(), course.getId());
            if (existing.isPresent()) {
                result.add(toDto(existing.get()));
                continue;
            }
            var savedEnrollment = enrollmentRepository.save(Enrollment.builder()
                    .student(student)
                    .course(course)
                    .payment(payment)
                    .order(order)
                    .orderItem(item)
                    .progress(BigDecimal.ZERO)
                    .completedLessonsCount(0)
                    .isCompleted(false)
                    .enrolledAt(LocalDateTime.now())
                    .build());

            course.setEnrollmentCount(course.getEnrollmentCount() + 1);
            courseRepository.save(course);

            var instructor = course.getInstructor();
            if (instructor != null) {
                instructor.setTotalStudents(instructor.getTotalStudents() + 1);
                instructorProfileRepository.save(instructor);
            }

            var coursePrice = item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO;
            if (instructor != null) {
                creditInstructorEarning(instructor.getId(), coursePrice);
            }
            try {
                var referrerIdStr = payment.getReferralCode();
                if (referrerIdStr != null && !referrerIdStr.isBlank() && coursePrice.compareTo(BigDecimal.ZERO) > 0) {
                    var referrerId = UUID.fromString(referrerIdStr.trim());
                    if (!referrerId.equals(student.getId())) {
                        var referralAmount = coursePrice.multiply(new BigDecimal("0.05")).setScale(2, RoundingMode.HALF_UP);
                        referralBalanceService.creditReferrer(referrerId, savedEnrollment.getId(), referralAmount);
                    }
                }
            } catch (Exception e) {
                // ignore invalid referrer id
            }
            emailLogService.recordEmail(
                    student.getId(),
                    student.getEmail(),
                    "Enrollment confirmation: " + (course.getTitle() != null ? course.getTitle() : "Course"),
                    "ENROLLMENT",
                    "SENT"
            );
            result.add(toDto(savedEnrollment));
        }
        return result;
    }

    private void creditInstructorEarning(UUID instructorProfileId, BigDecimal amount) {
        if (instructorProfileId == null) return;
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) return;
        InstructorEarning earning = instructorEarningRepository
                .findFirstByInstructorProfile_Id(instructorProfileId)
                .orElseGet(() -> instructorEarningRepository.save(InstructorEarning.builder()
                        .instructorProfile(instructorProfileRepository.findById(instructorProfileId).orElse(null))
                        .totalEarnings(BigDecimal.ZERO)
                        .totalWithdrawn(BigDecimal.ZERO)
                        .currentBalance(BigDecimal.ZERO)
                        .lastMonthEarning(BigDecimal.ZERO)
                        .build()));
        BigDecimal total = earning.getTotalEarnings() != null ? earning.getTotalEarnings() : BigDecimal.ZERO;
        BigDecimal balance = earning.getCurrentBalance() != null ? earning.getCurrentBalance() : BigDecimal.ZERO;
        earning.setTotalEarnings(total.add(amount));
        earning.setCurrentBalance(balance.add(amount));
        instructorEarningRepository.save(earning);
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
    public List<EnrollmentDTO> getMyInstructorEnrollments() {
        var instructorUser = resolveAuthenticatedUser();
        var enrollments = enrollmentRepository.findAllByCourse_Instructor_User_Id(instructorUser.getId());
        return enrollments.stream().map(this::toDto).toList();
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
        var student = enrollment.getStudent();
        var course = enrollment.getCourse();
        return EnrollmentDTO.builder()
                .id(enrollment.getId())
                .student(student != null ? UserDTO.builder()
                        .id(student.getId())
                        .firstName(student.getFirstName())
                        .lastName(student.getLastName())
                        .email(student.getEmail())
                        .build() : null)
                .course(course != null ? CourseDTO.builder()
                        .id(course.getId())
                        .title(course.getTitle())
                        .build() : null)
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
