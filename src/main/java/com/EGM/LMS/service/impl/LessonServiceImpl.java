package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.CourseSectionDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.model.Lesson;
import com.EGM.LMS.repository.CourseSectionRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseSectionRepository courseSectionRepository;

    @Override
    public LessonDTO createLesson(LessonDTO lesson) {
        return toDto(lessonRepository.save(toEntity(lesson)));
    }

    @Override
    public List<LessonDTO> getAllLessons() {
        var lessons = lessonRepository.findAll();
        var lessonDtos = new java.util.ArrayList<LessonDTO>();
        for (Lesson lesson : lessons) {
            lessonDtos.add(toDto(lesson));
        }
        return lessonDtos;
    }

    @Override
    public LessonDTO getLesson(UUID lessonId) {
        return toDto(lessonRepository.findById(lessonId).orElseThrow());
    }

    @Override
    public LessonDTO updateLesson(UUID lessonId, LessonDTO lesson) {
        lessonRepository.findById(lessonId).orElseThrow();
        var entity = toEntity(lesson);
        entity.setId(lessonId);
        return toDto(lessonRepository.save(entity));
    }

    @Override
    public void deleteLesson(UUID lessonId) {
        lessonRepository.deleteById(lessonId);
    }

    private Lesson toEntity(LessonDTO lesson) {
        var sectionId = lesson.getSection() != null ? lesson.getSection().getId() : null;
        return Lesson.builder()
                .section(sectionId != null ? courseSectionRepository.findById(sectionId).orElse(null) : null)
                .title(lesson.getTitle())
                .titleAm(lesson.getTitleAm())
                .titleOm(lesson.getTitleOm())
                .titleGz(lesson.getTitleGz())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .videoUrl240p(lesson.getVideoUrl240p())
                .videoUrl360p(lesson.getVideoUrl360p())
                .videoUrl720p(lesson.getVideoUrl720p())
                .encryptedVideoUrl(lesson.getEncryptedVideoUrl())
                .duration(lesson.getDuration())
                .documentUrl(lesson.getDocumentUrl())
                .documentType(lesson.getDocumentType())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .isDownloadable(lesson.getIsDownloadable())
                .isPublished(lesson.getIsPublished())
                .build();
    }

    private LessonDTO toDto(Lesson lesson) {
        return LessonDTO.builder()
                .id(lesson.getId())
                .section(lesson.getSection() != null ? CourseSectionDTO.builder().id(lesson.getSection().getId()).build() : null)
                .title(lesson.getTitle())
                .titleAm(lesson.getTitleAm())
                .titleOm(lesson.getTitleOm())
                .titleGz(lesson.getTitleGz())
                .type(lesson.getType())
                .videoUrl(lesson.getVideoUrl())
                .videoUrl240p(lesson.getVideoUrl240p())
                .videoUrl360p(lesson.getVideoUrl360p())
                .videoUrl720p(lesson.getVideoUrl720p())
                .encryptedVideoUrl(lesson.getEncryptedVideoUrl())
                .duration(lesson.getDuration())
                .documentUrl(lesson.getDocumentUrl())
                .documentType(lesson.getDocumentType())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .isFree(lesson.getIsFree())
                .isDownloadable(lesson.getIsDownloadable())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
