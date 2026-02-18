package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseCategoryRepository extends JpaRepository<CourseCategory, UUID> {
//    Course findById(UUID courseId);
}
