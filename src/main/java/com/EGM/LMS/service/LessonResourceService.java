package com.EGM.LMS.service;

import com.EGM.LMS.dto.LessonResourceDTO;

import java.util.List;
import java.util.UUID;

public interface LessonResourceService {
    LessonResourceDTO createLessonResource(LessonResourceDTO lessonResource);

    List<LessonResourceDTO> getAllLessonResources(UUID lessonId, UUID courseId);

    LessonResourceDTO getLessonResource(UUID lessonResourceId);

    LessonResourceDTO updateLessonResource(UUID lessonResourceId, LessonResourceDTO lessonResource);

    void deleteLessonResource(UUID lessonResourceId);
}
