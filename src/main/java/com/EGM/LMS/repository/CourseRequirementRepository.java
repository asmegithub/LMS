package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseRequirement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRequirementRepository extends JpaRepository<CourseRequirement, UUID> {
}
