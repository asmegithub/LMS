package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseRating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseRatingRepository extends JpaRepository<CourseRating, UUID> {
}
