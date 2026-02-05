package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseCategoryDTO;
import com.EGM.LMS.model.CourseCategory;
import com.EGM.LMS.repository.CourseCategoryRepository;
import com.EGM.LMS.service.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseCategoryServiceImpl implements CourseCategoryService {
    private final CourseCategoryRepository courseCategoryRepository;

    @Override
    public CourseCategoryDTO createCourseCategory(CourseCategoryDTO courseCategory) {
        return toDto(courseCategoryRepository.save(toEntity(courseCategory)));
    }

    @Override
    public List<CourseCategoryDTO> getAllCourseCategories() {
        var categories = courseCategoryRepository.findAll();
        var categoryDtos = new java.util.ArrayList<CourseCategoryDTO>();
        for (CourseCategory category : categories) {
            categoryDtos.add(toDto(category));
        }
        return categoryDtos;
    }

    @Override
    public CourseCategoryDTO getCourseCategory(UUID courseCategoryId) {
        return toDto(courseCategoryRepository.findById(courseCategoryId).orElseThrow());
    }

    @Override
    public CourseCategoryDTO updateCourseCategory(UUID courseCategoryId, CourseCategoryDTO courseCategory) {
        courseCategoryRepository.findById(courseCategoryId).orElseThrow();
        var entity = toEntity(courseCategory);
        entity.setId(courseCategoryId);
        return toDto(courseCategoryRepository.save(entity));
    }

    @Override
    public void deleteCourseCategory(UUID courseCategoryId) {
        courseCategoryRepository.deleteById(courseCategoryId);
    }

    private CourseCategory toEntity(CourseCategoryDTO courseCategory) {
        return CourseCategory.builder()
                .name(courseCategory.getName())
                .nameAm(courseCategory.getNameAm())
                .nameOm(courseCategory.getNameOm())
                .nameGz(courseCategory.getNameGz())
                .slug(courseCategory.getSlug())
                .description(courseCategory.getDescription())
                .icon(courseCategory.getIcon())
                .parentId(courseCategory.getParentId())
                .orderIndex(courseCategory.getOrderIndex())
                .isActive(courseCategory.isActive())
                .build();
    }

    private CourseCategoryDTO toDto(CourseCategory courseCategory) {
        return CourseCategoryDTO.builder()
                .id(courseCategory.getId())
                .name(courseCategory.getName())
                .nameAm(courseCategory.getNameAm())
                .nameOm(courseCategory.getNameOm())
                .nameGz(courseCategory.getNameGz())
                .slug(courseCategory.getSlug())
                .description(courseCategory.getDescription())
                .icon(courseCategory.getIcon())
                .parentId(courseCategory.getParentId())
                .orderIndex(courseCategory.getOrderIndex())
                .isActive(courseCategory.getIsActive())
                .createdAt(courseCategory.getCreatedAt())
                .updatedAt(courseCategory.getUpdatedAt())
                .build();
    }
}
