package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseSectionDTO;

import java.util.List;
import java.util.UUID;

public interface CourseSectionService {
    CourseSectionDTO createCourseSection(CourseSectionDTO courseSection);
    List<CourseSectionDTO> getAllCourseSections();
    CourseSectionDTO getCourseSection(UUID courseSectionId);
    CourseSectionDTO updateCourseSection(UUID courseSectionId, CourseSectionDTO courseSection);
    void deleteCourseSection(UUID courseSectionId);
}
