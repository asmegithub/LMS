package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.CourseRequirementDTO;
import com.EGM.LMS.model.CourseRequirement;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.CourseRequirementRepository;
import com.EGM.LMS.service.CourseRequirementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CourseRequirementServiceImpl implements CourseRequirementService {
    private final CourseRequirementRepository courseRequirementRepository;
    private final CourseRepository courseRepository;

    @Override
    public CourseRequirementDTO createCourseRequirement(CourseRequirementDTO courseRequirement) {
        return toDto(courseRequirementRepository.save(toEntity(courseRequirement)));
    }

    @Override
    public List<CourseRequirementDTO> getAllCourseRequirements(UUID courseId) {
        var requirements = courseId != null
                ? courseRequirementRepository.findAllByCourse_Id(courseId)
                : courseRequirementRepository.findAll();
        var requirementDtos = new java.util.ArrayList<CourseRequirementDTO>();
        for (CourseRequirement requirement : requirements) {
            requirementDtos.add(toDto(requirement));
        }
        return requirementDtos;
    }

    @Override
    public CourseRequirementDTO getCourseRequirement(UUID courseRequirementId) {
        return toDto(courseRequirementRepository.findById(courseRequirementId).orElseThrow());
    }

    @Override
    public CourseRequirementDTO updateCourseRequirement(UUID courseRequirementId, CourseRequirementDTO courseRequirement) {
        courseRequirementRepository.findById(courseRequirementId).orElseThrow();
        var entity = toEntity(courseRequirement);
        entity.setId(courseRequirementId);
        return toDto(courseRequirementRepository.save(entity));
    }

    @Override
    public void deleteCourseRequirement(UUID courseRequirementId) {
        courseRequirementRepository.deleteById(courseRequirementId);
    }

    private CourseRequirement toEntity(CourseRequirementDTO courseRequirement) {
        var courseId = courseRequirement.getCourse() != null ? courseRequirement.getCourse().getId() : null;
        return CourseRequirement.builder()
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .text(courseRequirement.getText())
                .textAm(courseRequirement.getTextAm())
                .textOm(courseRequirement.getTextOm())
                .textGz(courseRequirement.getTextGz())
            .orderIndex(courseRequirement.getOrderIndex() != null ? courseRequirement.getOrderIndex() : 0)
                .build();
    }

    private CourseRequirementDTO toDto(CourseRequirement courseRequirement) {
        return CourseRequirementDTO.builder()
                .id(courseRequirement.getId())
                .course(courseRequirement.getCourse() != null ? CourseDTO.builder().id(courseRequirement.getCourse().getId()).build() : null)
                .text(courseRequirement.getText())
                .textAm(courseRequirement.getTextAm())
                .textOm(courseRequirement.getTextOm())
                .textGz(courseRequirement.getTextGz())
                .orderIndex(courseRequirement.getOrderIndex())
                .createdAt(courseRequirement.getCreatedAt())
                .updatedAt(courseRequirement.getUpdatedAt())
                .build();
    }
}
