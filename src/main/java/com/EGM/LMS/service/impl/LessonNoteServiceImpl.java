package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.LessonNoteDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.LessonNote;
import com.EGM.LMS.repository.LessonNoteRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.LessonNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonNoteServiceImpl implements LessonNoteService {
    private final LessonNoteRepository lessonNoteRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public LessonNoteDTO createLessonNote(LessonNoteDTO lessonNote) {
        return toDto(lessonNoteRepository.save(toEntity(lessonNote)));
    }

    @Override
    public List<LessonNoteDTO> getAllLessonNotes() {
        var notes = lessonNoteRepository.findAll();
        var noteDtos = new java.util.ArrayList<LessonNoteDTO>();
        for (LessonNote note : notes) {
            noteDtos.add(toDto(note));
        }
        return noteDtos;
    }

    @Override
    public LessonNoteDTO getLessonNote(UUID lessonNoteId) {
        return toDto(lessonNoteRepository.findById(lessonNoteId).orElseThrow());
    }

    @Override
    public LessonNoteDTO updateLessonNote(UUID lessonNoteId, LessonNoteDTO lessonNote) {
        lessonNoteRepository.findById(lessonNoteId).orElseThrow();
        var entity = toEntity(lessonNote);
        entity.setId(lessonNoteId);
        return toDto(lessonNoteRepository.save(entity));
    }

    @Override
    public void deleteLessonNote(UUID lessonNoteId) {
        lessonNoteRepository.deleteById(lessonNoteId);
    }

    private LessonNote toEntity(LessonNoteDTO lessonNote) {
        var lessonId = lessonNote.getLesson() != null ? lessonNote.getLesson().getId() : null;
        var studentId = lessonNote.getStudent() != null ? lessonNote.getStudent().getId() : null;
        return LessonNote.builder()
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .content(lessonNote.getContent())
                .timestamp(lessonNote.getTimestamp())
                .build();
    }

    private LessonNoteDTO toDto(LessonNote lessonNote) {
        return LessonNoteDTO.builder()
                .id(lessonNote.getId())
                .lesson(lessonNote.getLesson() != null ? LessonDTO.builder().id(lessonNote.getLesson().getId()).build() : null)
                .student(lessonNote.getStudent() != null ? UserDTO.builder().id(lessonNote.getStudent().getId()).build() : null)
                .content(lessonNote.getContent())
                .timestamp(lessonNote.getTimestamp())
                .createdAt(lessonNote.getCreatedAt())
                .updatedAt(lessonNote.getUpdatedAt())
                .build();
    }
}
