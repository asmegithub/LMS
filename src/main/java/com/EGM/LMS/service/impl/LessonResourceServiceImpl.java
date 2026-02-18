package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.LessonResourceDTO;
import com.EGM.LMS.model.LessonResource;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.LessonResourceRepository;
import com.EGM.LMS.service.LessonResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonResourceServiceImpl implements LessonResourceService {
    private final LessonResourceRepository lessonResourceRepository;
    private final LessonRepository lessonRepository;

    @Override
    public LessonResourceDTO createLessonResource(LessonResourceDTO lessonResource) {
        return toDto(lessonResourceRepository.save(toEntity(lessonResource)));
    }

    @Override
    public List<LessonResourceDTO> getAllLessonResources() {
        var resources = lessonResourceRepository.findAll();
        var resourceDtos = new java.util.ArrayList<LessonResourceDTO>();
        for (LessonResource resource : resources) {
            resourceDtos.add(toDto(resource));
        }
        return resourceDtos;
    }

    @Override
    public LessonResourceDTO getLessonResource(UUID lessonResourceId) {
        return toDto(lessonResourceRepository.findById(lessonResourceId).orElseThrow());
    }

    @Override
    public LessonResourceDTO updateLessonResource(UUID lessonResourceId, LessonResourceDTO lessonResource) {
        lessonResourceRepository.findById(lessonResourceId).orElseThrow();
        var entity = toEntity(lessonResource);
        entity.setId(lessonResourceId);
        return toDto(lessonResourceRepository.save(entity));
    }

    @Override
    public void deleteLessonResource(UUID lessonResourceId) {
        lessonResourceRepository.deleteById(lessonResourceId);
    }

    private LessonResource toEntity(LessonResourceDTO lessonResource) {
        var lessonId = lessonResource.getLesson() != null ? lessonResource.getLesson().getId() : null;
        return LessonResource.builder()
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .title(lessonResource.getTitle())
                .titleAm(lessonResource.getTitleAm())
                .titleOm(lessonResource.getTitleOm())
                .titleGz(lessonResource.getTitleGz())
                .type(lessonResource.getType())
                .url(lessonResource.getUrl())
                .fileSize(lessonResource.getFileSize() != null ? lessonResource.getFileSize() : 0)
                .orderIndex(lessonResource.getOrderIndex() != null ? lessonResource.getOrderIndex() : 0)
                .build();
    }

    private LessonResourceDTO toDto(LessonResource lessonResource) {
        return LessonResourceDTO.builder()
                .id(lessonResource.getId())
                .lesson(lessonResource.getLesson() != null ? LessonDTO.builder().id(lessonResource.getLesson().getId()).build() : null)
                .title(lessonResource.getTitle())
                .titleAm(lessonResource.getTitleAm())
                .titleOm(lessonResource.getTitleOm())
                .titleGz(lessonResource.getTitleGz())
                .type(lessonResource.getType())
                .url(lessonResource.getUrl())
                .fileSize(lessonResource.getFileSize())
                .orderIndex(lessonResource.getOrderIndex())
                .createdAt(lessonResource.getCreatedAt())
                .updatedAt(lessonResource.getUpdatedAt())
                .build();
    }
}
