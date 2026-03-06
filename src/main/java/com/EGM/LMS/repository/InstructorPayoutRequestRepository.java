package com.EGM.LMS.repository;

import com.EGM.LMS.model.InstructorPayoutRequest;

import java.util.List;
import java.util.UUID;

public interface InstructorPayoutRequestRepository extends org.springframework.data.jpa.repository.JpaRepository<InstructorPayoutRequest, UUID> {
    List<InstructorPayoutRequest> findByInstructorProfile_IdOrderByCreatedAtDesc(UUID instructorProfileId);
}
