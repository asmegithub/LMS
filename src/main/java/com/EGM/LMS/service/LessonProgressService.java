package com.EGM.LMS.service;

import com.EGM.LMS.dto.LessonProgressDTO;

import java.util.List;
import java.util.UUID;

public interface LessonProgressService {
    LessonProgressDTO createLessonProgress(LessonProgressDTO lessonProgress);
    List<LessonProgressDTO> getAllLessonProgresses();
    LessonProgressDTO getLessonProgress(UUID lessonProgressId);
    LessonProgressDTO updateLessonProgress(UUID lessonProgressId, LessonProgressDTO lessonProgress);
    void deleteLessonProgress(UUID lessonProgressId);
}
