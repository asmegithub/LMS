package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.LessonDiscussionDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.LessonDiscussion;
import com.EGM.LMS.repository.LessonDiscussionRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.LessonDiscussionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonDiscussionServiceImpl implements LessonDiscussionService {
    private final LessonDiscussionRepository lessonDiscussionRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public LessonDiscussionDTO createLessonDiscussion(LessonDiscussionDTO lessonDiscussion) {
        return toDto(lessonDiscussionRepository.save(toEntity(lessonDiscussion)));
    }

    @Override
    public List<LessonDiscussionDTO> getAllLessonDiscussions() {
        var discussions = lessonDiscussionRepository.findAll();
        var discussionDtos = new java.util.ArrayList<LessonDiscussionDTO>();
        for (LessonDiscussion discussion : discussions) {
            discussionDtos.add(toDto(discussion));
        }
        return discussionDtos;
    }

    @Override
    public LessonDiscussionDTO getLessonDiscussion(UUID lessonDiscussionId) {
        return toDto(lessonDiscussionRepository.findById(lessonDiscussionId).orElseThrow());
    }

    @Override
    public LessonDiscussionDTO updateLessonDiscussion(UUID lessonDiscussionId, LessonDiscussionDTO lessonDiscussion) {
        lessonDiscussionRepository.findById(lessonDiscussionId).orElseThrow();
        var entity = toEntity(lessonDiscussion);
        entity.setId(lessonDiscussionId);
        return toDto(lessonDiscussionRepository.save(entity));
    }

    @Override
    public void deleteLessonDiscussion(UUID lessonDiscussionId) {
        lessonDiscussionRepository.deleteById(lessonDiscussionId);
    }

    private LessonDiscussion toEntity(LessonDiscussionDTO lessonDiscussion) {
        var lessonId = lessonDiscussion.getLesson() != null ? lessonDiscussion.getLesson().getId() : null;
        var userId = lessonDiscussion.getUser() != null ? lessonDiscussion.getUser().getId() : null;
        return LessonDiscussion.builder()
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .content(lessonDiscussion.getContent())
                .isPinned(lessonDiscussion.getIsPinned())
                .build();
    }

    private LessonDiscussionDTO toDto(LessonDiscussion lessonDiscussion) {
        return LessonDiscussionDTO.builder()
                .id(lessonDiscussion.getId())
                .lesson(lessonDiscussion.getLesson() != null ? LessonDTO.builder().id(lessonDiscussion.getLesson().getId()).build() : null)
                .user(lessonDiscussion.getUser() != null ? UserDTO.builder().id(lessonDiscussion.getUser().getId()).build() : null)
                .content(lessonDiscussion.getContent())
                .isPinned(lessonDiscussion.getIsPinned())
                .createdAt(lessonDiscussion.getCreatedAt())
                .updatedAt(lessonDiscussion.getUpdatedAt())
                .build();
    }
}
