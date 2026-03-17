package com.EGM.LMS.repository;

import com.EGM.LMS.model.InstructorPayoutRequest;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InstructorPayoutRequestRepository extends org.springframework.data.jpa.repository.JpaRepository<InstructorPayoutRequest, UUID> {
    List<InstructorPayoutRequest> findByInstructorProfile_IdOrderByCreatedAtDesc(UUID instructorProfileId);
    List<InstructorPayoutRequest> findByStatusIgnoreCaseOrderByCreatedAtDesc(String status);

    @Query("select coalesce(sum(r.amount), 0) from InstructorPayoutRequest r where r.instructorProfile.id = :instructorProfileId and lower(r.status) = lower('PENDING')")
    java.math.BigDecimal sumPendingAmount(UUID instructorProfileId);
}
