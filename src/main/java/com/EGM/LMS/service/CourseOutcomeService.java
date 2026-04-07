package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseOutcomeDTO;

import java.util.List;
import java.util.UUID;

public interface CourseOutcomeService {
    CourseOutcomeDTO createCourseOutcome(CourseOutcomeDTO courseOutcome);
    List<CourseOutcomeDTO> getAllCourseOutcomes(UUID courseId);
    CourseOutcomeDTO getCourseOutcome(UUID courseOutcomeId);
    CourseOutcomeDTO updateCourseOutcome(UUID courseOutcomeId, CourseOutcomeDTO courseOutcome);
    void deleteCourseOutcome(UUID courseOutcomeId);
}
