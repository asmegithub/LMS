package com.EGM.LMS.service;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.InstructorEnrollmentSummaryDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollment);
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollment(UUID enrollmentId);
    List<EnrollmentDTO> getMyEnrollments();
    Optional<EnrollmentDTO> getMyEnrollmentByCourse(UUID courseId);
    InstructorEnrollmentSummaryDTO getMyInstructorEnrollmentSummary();
    EnrollmentDTO updateEnrollment(UUID enrollmentId, EnrollmentDTO enrollment);
    void deleteEnrollment(UUID enrollmentId);
}
