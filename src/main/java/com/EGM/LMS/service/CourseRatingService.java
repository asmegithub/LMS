package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseRatingDTO;

import java.util.List;
import java.util.UUID;

public interface CourseRatingService {
    CourseRatingDTO createCourseRating(CourseRatingDTO courseRating);
    List<CourseRatingDTO> getAllCourseRatings();
    CourseRatingDTO getCourseRating(UUID courseRatingId);
    CourseRatingDTO updateCourseRating(UUID courseRatingId, CourseRatingDTO courseRating);
    void deleteCourseRating(UUID courseRatingId);
}
