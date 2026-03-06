package com.EGM.LMS.repository;

import com.EGM.LMS.model.InstructorBankDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstructorBankDetailRepository extends JpaRepository<InstructorBankDetail, UUID> {
    java.util.List<InstructorBankDetail> findByInstructorProfile_IdOrderByIsPrimaryDesc(UUID instructorProfileId);
}
