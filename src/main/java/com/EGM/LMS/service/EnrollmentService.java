package com.EGM.LMS.service;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.InstructorEnrollmentSummaryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollment);
    /** Creates enrollment from a completed payment (e.g. after Chapa callback). No auth required. */
    EnrollmentDTO createEnrollmentForPayment(UUID paymentId);
    /** Creates one enrollment per order item for a completed order. No auth required. */
    List<EnrollmentDTO> createEnrollmentsForOrder(UUID orderId, UUID paymentId);
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollment(UUID enrollmentId);
    List<EnrollmentDTO> getMyEnrollments();
    Optional<EnrollmentDTO> getMyEnrollmentByCourse(UUID courseId);
    InstructorEnrollmentSummaryDTO getMyInstructorEnrollmentSummary();
    List<EnrollmentDTO> getMyInstructorEnrollments();
    EnrollmentDTO updateEnrollment(UUID enrollmentId, EnrollmentDTO enrollment);
    void deleteEnrollment(UUID enrollmentId);
}
