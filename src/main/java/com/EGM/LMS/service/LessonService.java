package com.EGM.LMS.service;

import com.EGM.LMS.dto.LessonDTO;

import java.util.List;
import java.util.UUID;

public interface LessonService {
    LessonDTO createLesson(LessonDTO lesson);
    List<LessonDTO> getAllLessons();
    List<LessonDTO> getLessonsByCourseId(UUID courseId);
    LessonDTO getLesson(UUID lessonId);
    LessonDTO updateLesson(UUID lessonId, LessonDTO lesson);
    void deleteLesson(UUID lessonId);
}
