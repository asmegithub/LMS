package com.EGM.LMS.repository;

import com.EGM.LMS.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EnrollmentRepository extends JpaRepository<Enrollment, UUID> {
	Optional<Enrollment> findFirstByStudent_IdAndCourse_Id(UUID studentId, UUID courseId);
	List<Enrollment> findAllByStudent_Id(UUID studentId);
	List<Enrollment> findAllByCourse_Id(UUID courseId);
	List<Enrollment> findAllByCourse_Instructor_User_Id(UUID instructorUserId);
}
