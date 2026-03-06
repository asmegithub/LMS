package com.EGM.LMS.repository;

import com.EGM.LMS.model.InstructorEarning;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstructorEarningRepository extends JpaRepository<InstructorEarning, UUID> {
    java.util.Optional<InstructorEarning> findFirstByInstructorProfile_Id(UUID instructorProfileId);
}
