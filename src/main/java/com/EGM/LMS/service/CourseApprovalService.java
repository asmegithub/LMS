package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseApprovalDTO;

import java.util.List;
import java.util.UUID;

public interface CourseApprovalService {
    CourseApprovalDTO createCourseApproval(CourseApprovalDTO courseApproval);
    List<CourseApprovalDTO> getAllCourseApprovals();
    CourseApprovalDTO getCourseApproval(UUID courseApprovalId);
    CourseApprovalDTO updateCourseApproval(UUID courseApprovalId, CourseApprovalDTO courseApproval);
    void deleteCourseApproval(UUID courseApprovalId);
}
