package com.EGM.LMS.repository;

import com.EGM.LMS.model.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, UUID> {
}
