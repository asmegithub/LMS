package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseOutcomeRepository extends JpaRepository<CourseOutcome, UUID> {
}
