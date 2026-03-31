package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseOutcome;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseOutcomeRepository extends JpaRepository<CourseOutcome, UUID> {
    List<CourseOutcome> findAllByCourse_Id(UUID courseId);
}
