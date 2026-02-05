package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.model.Course;
import com.EGM.LMS.repository.CourseCategoryRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.InstructorProfileRepository;
import com.EGM.LMS.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseCategoryRepository courseCategoryRepository;
    private final InstructorProfileRepository instructorProfileRepository;
    private  final CourseRepository courseRepository;

    @Override
    public CourseDTO createCourse(CourseDTO coursedto) {
        return toDto(courseRepository.save(toEntity(coursedto)));
    }

    @Override
    public List<CourseDTO> getAllCourses() {
        var courses=courseRepository.findAll();
        var coursesDtos= new ArrayList<CourseDTO>();
        for (Course c:courses){
            coursesDtos.add(toDto(c));
        }
        return coursesDtos;
    }

    @Override
    public CourseDTO getCourse(UUID courseId) {
        var course=courseRepository.findById(courseId).orElseThrow();
        return  toDto(course);

    }

    @Override
    public CourseDTO updateCourse(UUID courseId,CourseDTO coursedto) {
        var existingCourse=courseRepository.findById(courseId).orElseThrow();
        existingCourse.setDescription(coursedto.getDescription());
        existingCourse.setDescriptionAm(coursedto.getDescriptionAm());
        existingCourse.setDescriptionGz(coursedto.getDescriptionGz());
        existingCourse.setDescriptionOm(coursedto.getDescriptionOm());
        existingCourse.setTitle(coursedto.getTitle());
        existingCourse.setTitleAm(coursedto.getTitleAm());
        existingCourse.setTitleGz(coursedto.getTitleGz());
        existingCourse.setTitleOm(coursedto.getTitleOm());
        existingCourse.setThumbnail(coursedto.getThumbnail());
        existingCourse.setPreviewVideo(coursedto.getPreviewVideo());
        existingCourse.setPrice(coursedto.getPrice());
        existingCourse.setDiscountPrice(coursedto.getDiscountPrice());
        existingCourse.setCurrency(coursedto.getCurrency());
        existingCourse.setLevel(coursedto.getLevel());
        existingCourse.setStatus(coursedto.getStatus());
        existingCourse.setTotalLessons(coursedto.getTotalLessons());
        existingCourse.setSlug(coursedto.getSlug());
        existingCourse.setCategory(courseCategoryRepository.findById(coursedto.getCategoryId()).orElse(null));
        existingCourse.setInstructor(instructorProfileRepository.findById(coursedto.getInstructorId()).orElse(null));

        return toDto(courseRepository.save(existingCourse));
    }

    @Override
    public void deleteCourse(UUID courseId) {
        courseRepository.deleteById(courseId);
    }

//    mapper methods
    Course toEntity(CourseDTO coursedto){
        return Course.builder()
                .category(courseCategoryRepository.findById(coursedto.getCategoryId()).orElse(null))
                .instructor(instructorProfileRepository.findById(coursedto.getInstructorId()).orElse(null))
                .title(coursedto.getTitle())
                .titleAm(coursedto.getTitleAm())
                .titleGz(coursedto.getTitleGz())
                .titleOm(coursedto.getTitleOm())

                .slug(coursedto.getSlug())
                .totalLessons(coursedto.getTotalLessons())

                .thumbnail(coursedto.getThumbnail())
                .previewVideo(coursedto.getPreviewVideo())
                .level(coursedto.getLevel())
                .status(coursedto.getStatus())

                .description(coursedto.getDescription())
                .descriptionAm(coursedto.getDescription())
                .descriptionGz(coursedto.getDescriptionGz())
                .descriptionOm(coursedto.getDescriptionOm())

                .price(coursedto.getPrice())
                .discountPrice(coursedto.getDiscountPrice())
                .currency(coursedto.getCurrency())

                .build();
    }

    CourseDTO toDto(Course course){
        return CourseDTO.builder()
                .id(course.getId())
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .instructorId(course.getInstructor() != null ? course.getInstructor().getId() : null)
                .title(course.getTitle())
                .titleAm(course.getTitleAm())
                .titleGz(course.getTitleGz())
                .titleOm(course.getTitleOm())

                .slug(course.getSlug())
                .totalLessons(course.getTotalLessons())

                .thumbnail(course.getThumbnail())
                .previewVideo(course.getPreviewVideo())
                .level(course.getLevel())
                .status(course.getStatus())

                .description(course.getDescription())
                .descriptionAm(course.getDescriptionAm())
                .descriptionGz(course.getDescriptionGz())
                .descriptionOm(course.getDescriptionOm())

                .price(course.getPrice())
                .discountPrice(course.getDiscountPrice())
                .currency(course.getCurrency())

                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }



}
