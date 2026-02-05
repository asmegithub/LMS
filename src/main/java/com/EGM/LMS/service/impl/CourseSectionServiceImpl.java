package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CourseSectionDTO;
import com.EGM.LMS.model.CourseSection;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CourseSectionRepository;
import com.EGM.LMS.service.CourseSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseSectionServiceImpl implements CourseSectionService {
    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;

    @Override
    public CourseSectionDTO createCourseSection(CourseSectionDTO courseSection) {
        return toDto(courseSectionRepository.save(toEntity(courseSection)));
    }

    @Override
    public List<CourseSectionDTO> getAllCourseSections() {
        var sections = courseSectionRepository.findAll();
        var sectionDtos = new java.util.ArrayList<CourseSectionDTO>();
        for (CourseSection section : sections) {
            sectionDtos.add(toDto(section));
        }
        return sectionDtos;
    }

    @Override
    public CourseSectionDTO getCourseSection(UUID courseSectionId) {
        return toDto(courseSectionRepository.findById(courseSectionId).orElseThrow());
    }

    @Override
    public CourseSectionDTO updateCourseSection(UUID courseSectionId, CourseSectionDTO courseSection) {
        courseSectionRepository.findById(courseSectionId).orElseThrow();
        var entity = toEntity(courseSection);
        entity.setId(courseSectionId);
        return toDto(courseSectionRepository.save(entity));
    }

    @Override
    public void deleteCourseSection(UUID courseSectionId) {
        courseSectionRepository.deleteById(courseSectionId);
    }

    private CourseSection toEntity(CourseSectionDTO courseSection) {
        var courseId = courseSection.getCourse() != null ? courseSection.getCourse().getId() : null;
        return CourseSection.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .title(courseSection.getTitle())
                .titleAm(courseSection.getTitleAm())
                .titleOm(courseSection.getTitleOm())
                .titleGz(courseSection.getTitleGz())
                .description(courseSection.getDescription())
                .orderIndex(courseSection.getOrderIndex())
                .build();
    }

    private CourseSectionDTO toDto(CourseSection courseSection) {
        return CourseSectionDTO.builder()
                .id(courseSection.getId())
                .course(courseSection.getCourse() != null ? CourseDTO.builder().id(courseSection.getCourse().getId()).build() : null)
                .title(courseSection.getTitle())
                .titleAm(courseSection.getTitleAm())
                .titleOm(courseSection.getTitleOm())
                .titleGz(courseSection.getTitleGz())
                .description(courseSection.getDescription())
                .orderIndex(courseSection.getOrderIndex())
                .createdAt(courseSection.getCreatedAt())
                .updatedAt(courseSection.getUpdatedAt())
                .build();
    }
}
