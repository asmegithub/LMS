package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {
}
