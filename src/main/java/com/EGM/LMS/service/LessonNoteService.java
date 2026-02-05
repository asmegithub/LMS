package com.EGM.LMS.service;

import com.EGM.LMS.dto.LessonNoteDTO;

import java.util.List;
import java.util.UUID;

public interface LessonNoteService {
    LessonNoteDTO createLessonNote(LessonNoteDTO lessonNote);
    List<LessonNoteDTO> getAllLessonNotes();
    LessonNoteDTO getLessonNote(UUID lessonNoteId);
    LessonNoteDTO updateLessonNote(UUID lessonNoteId, LessonNoteDTO lessonNote);
    void deleteLessonNote(UUID lessonNoteId);
}
