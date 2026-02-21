package com.EGM.LMS.repository;

import com.EGM.LMS.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
	List<Review> findAllByCourse_Id(UUID courseId);
	Optional<Review> findFirstByCourse_IdAndStudent_Id(UUID courseId, UUID studentId);
	List<Review> findAllByCourse_Instructor_Id(UUID instructorId);
}
