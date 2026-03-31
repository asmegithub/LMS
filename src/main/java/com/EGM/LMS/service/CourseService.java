package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseDTO;

import java.util.List;
import java.util.UUID;

public interface CourseService {
//    full crud for Courses
    CourseDTO createCourse(CourseDTO coursedto);
    List<CourseDTO> getAllCourses();
    List<CourseDTO> getCoursesByStatus(String status);
    CourseDTO getCourse(UUID courseId);
    CourseDTO updateCourse(UUID courseId,CourseDTO coursedto);
    /** Admin: toggle course featured flag. */
    CourseDTO setFeatured(UUID courseId, boolean isFeatured);
    /** Admin/instructor owner: toggle course visibility on public listings. */
    CourseDTO setPublished(UUID courseId, boolean isPublished);
    void deleteCourse(UUID courseId);


    
}
