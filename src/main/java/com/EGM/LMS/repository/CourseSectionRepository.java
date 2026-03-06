package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {

    List<CourseSection> findByCourse_IdOrderByOrderIndexAsc(UUID courseId);
}
