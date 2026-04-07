package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseRequirementDTO;

import java.util.List;
import java.util.UUID;

public interface CourseRequirementService {
    CourseRequirementDTO createCourseRequirement(CourseRequirementDTO courseRequirement);
    List<CourseRequirementDTO> getAllCourseRequirements(UUID courseId);
    CourseRequirementDTO getCourseRequirement(UUID courseRequirementId);
    CourseRequirementDTO updateCourseRequirement(UUID courseRequirementId, CourseRequirementDTO courseRequirement);
    void deleteCourseRequirement(UUID courseRequirementId);
}
