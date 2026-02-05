package com.EGM.LMS.service;

import com.EGM.LMS.dto.CourseCategoryDTO;

import java.util.List;
import java.util.UUID;

public interface CourseCategoryService {
    CourseCategoryDTO createCourseCategory(CourseCategoryDTO courseCategory);
    List<CourseCategoryDTO> getAllCourseCategories();
    CourseCategoryDTO getCourseCategory(UUID courseCategoryId);
    CourseCategoryDTO updateCourseCategory(UUID courseCategoryId, CourseCategoryDTO courseCategory);
    void deleteCourseCategory(UUID courseCategoryId);
}
