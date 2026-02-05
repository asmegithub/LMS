package com.EGM.LMS.service;

import com.EGM.LMS.dto.EnrollmentDTO;

import java.util.List;
import java.util.UUID;

public interface EnrollmentService {
    EnrollmentDTO createEnrollment(EnrollmentDTO enrollment);
    List<EnrollmentDTO> getAllEnrollments();
    EnrollmentDTO getEnrollment(UUID enrollmentId);
    EnrollmentDTO updateEnrollment(UUID enrollmentId, EnrollmentDTO enrollment);
    void deleteEnrollment(UUID enrollmentId);
}
