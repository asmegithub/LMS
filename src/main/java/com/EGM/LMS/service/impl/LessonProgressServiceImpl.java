package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.LessonProgressDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.LessonProgress;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.LessonProgressRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.LessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LessonProgressServiceImpl implements LessonProgressService {
    private final LessonProgressRepository lessonProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public LessonProgressDTO createLessonProgress(LessonProgressDTO lessonProgress) {
        return toDto(lessonProgressRepository.save(toEntity(lessonProgress)));
    }

    @Override
    public List<LessonProgressDTO> getAllLessonProgresses() {
        var progresses = lessonProgressRepository.findAll();
        var progressDtos = new java.util.ArrayList<LessonProgressDTO>();
        for (LessonProgress progress : progresses) {
            progressDtos.add(toDto(progress));
        }
        return progressDtos;
    }

    @Override
    public LessonProgressDTO getLessonProgress(UUID lessonProgressId) {
        return toDto(lessonProgressRepository.findById(lessonProgressId).orElseThrow());
    }

    @Override
    public LessonProgressDTO updateLessonProgress(UUID lessonProgressId, LessonProgressDTO lessonProgress) {
        lessonProgressRepository.findById(lessonProgressId).orElseThrow();
        var entity = toEntity(lessonProgress);
        entity.setId(lessonProgressId);
        return toDto(lessonProgressRepository.save(entity));
    }

    @Override
    public void deleteLessonProgress(UUID lessonProgressId) {
        lessonProgressRepository.deleteById(lessonProgressId);
    }

    private LessonProgress toEntity(LessonProgressDTO lessonProgress) {
        var enrollmentId = lessonProgress.getEnrollment() != null ? lessonProgress.getEnrollment().getId() : null;
        var lessonId = lessonProgress.getLesson() != null ? lessonProgress.getLesson().getId() : null;
        var studentId = lessonProgress.getStudent() != null ? lessonProgress.getStudent().getId() : null;
        return LessonProgress.builder()
                .enrollment(enrollmentId != null ? enrollmentRepository.findById(enrollmentId).orElse(null) : null)
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .status(lessonProgress.getStatus())
                .completedAt(lessonProgress.getCompletedAt())
                .build();
    }

    private LessonProgressDTO toDto(LessonProgress lessonProgress) {
        return LessonProgressDTO.builder()
                .id(lessonProgress.getId())
                .enrollment(lessonProgress.getEnrollment() != null ? EnrollmentDTO.builder().id(lessonProgress.getEnrollment().getId()).build() : null)
                .lesson(lessonProgress.getLesson() != null ? LessonDTO.builder().id(lessonProgress.getLesson().getId()).build() : null)
                .student(lessonProgress.getStudent() != null ? UserDTO.builder().id(lessonProgress.getStudent().getId()).build() : null)
                .status(lessonProgress.getStatus())
                .completedAt(lessonProgress.getCompletedAt())
                .createdAt(lessonProgress.getCreatedAt())
                .updatedAt(lessonProgress.getUpdatedAt())
                .build();
    }
}
