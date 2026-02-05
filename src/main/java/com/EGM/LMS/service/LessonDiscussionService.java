package com.EGM.LMS.service;

import com.EGM.LMS.dto.LessonDiscussionDTO;

import java.util.List;
import java.util.UUID;

public interface LessonDiscussionService {
    LessonDiscussionDTO createLessonDiscussion(LessonDiscussionDTO lessonDiscussion);
    List<LessonDiscussionDTO> getAllLessonDiscussions();
    LessonDiscussionDTO getLessonDiscussion(UUID lessonDiscussionId);
    LessonDiscussionDTO updateLessonDiscussion(UUID lessonDiscussionId, LessonDiscussionDTO lessonDiscussion);
    void deleteLessonDiscussion(UUID lessonDiscussionId);
}
