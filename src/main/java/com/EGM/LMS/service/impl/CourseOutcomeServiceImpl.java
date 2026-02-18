package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CourseOutcomeDTO;
import com.EGM.LMS.model.CourseOutcome;
import com.EGM.LMS.repository.CourseOutcomeRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.service.CourseOutcomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseOutcomeServiceImpl implements CourseOutcomeService {
    private final CourseOutcomeRepository courseOutcomeRepository;
    private final CourseRepository courseRepository;

    @Override
    public CourseOutcomeDTO createCourseOutcome(CourseOutcomeDTO courseOutcome) {
        return toDto(courseOutcomeRepository.save(toEntity(courseOutcome)));
    }

    @Override
    public List<CourseOutcomeDTO> getAllCourseOutcomes() {
        var outcomes = courseOutcomeRepository.findAll();
        var outcomeDtos = new java.util.ArrayList<CourseOutcomeDTO>();
        for (CourseOutcome outcome : outcomes) {
            outcomeDtos.add(toDto(outcome));
        }
        return outcomeDtos;
    }

    @Override
    public CourseOutcomeDTO getCourseOutcome(UUID courseOutcomeId) {
        return toDto(courseOutcomeRepository.findById(courseOutcomeId).orElseThrow());
    }

    @Override
    public CourseOutcomeDTO updateCourseOutcome(UUID courseOutcomeId, CourseOutcomeDTO courseOutcome) {
        courseOutcomeRepository.findById(courseOutcomeId).orElseThrow();
        var entity = toEntity(courseOutcome);
        entity.setId(courseOutcomeId);
        return toDto(courseOutcomeRepository.save(entity));
    }

    @Override
    public void deleteCourseOutcome(UUID courseOutcomeId) {
        courseOutcomeRepository.deleteById(courseOutcomeId);
    }

    private CourseOutcome toEntity(CourseOutcomeDTO courseOutcome) {
        var courseId = courseOutcome.getCourse() != null ? courseOutcome.getCourse().getId() : null;
        return CourseOutcome.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .text(courseOutcome.getText())
                .textAm(courseOutcome.getTextAm())
                .textOm(courseOutcome.getTextOm())
                .textGz(courseOutcome.getTextGz())
            .orderIndex(courseOutcome.getOrderIndex() != null ? courseOutcome.getOrderIndex() : 0)
                .build();
    }

    private CourseOutcomeDTO toDto(CourseOutcome courseOutcome) {
        return CourseOutcomeDTO.builder()
                .id(courseOutcome.getId())
                .course(courseOutcome.getCourse() != null ? CourseDTO.builder().id(courseOutcome.getCourse().getId()).build() : null)
                .text(courseOutcome.getText())
                .textAm(courseOutcome.getTextAm())
                .textOm(courseOutcome.getTextOm())
                .textGz(courseOutcome.getTextGz())
                .orderIndex(courseOutcome.getOrderIndex())
                .createdAt(courseOutcome.getCreatedAt())
                .updatedAt(courseOutcome.getUpdatedAt())
                .build();
    }
}
