package com.EGM.LMS.repository;

import com.EGM.LMS.model.CourseApproval;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CourseApprovalRepository extends JpaRepository<CourseApproval, UUID> {
}
