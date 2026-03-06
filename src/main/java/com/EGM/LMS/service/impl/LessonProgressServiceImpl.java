package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.LessonProgressDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Enrollment;
import com.EGM.LMS.model.LessonProgress;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.LessonProgressRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.LessonProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
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
    public List<LessonProgressDTO> getByEnrollmentId(UUID enrollmentId) {
        var progresses = lessonProgressRepository.findByEnrollment_IdOrderByCreatedAtAsc(enrollmentId);
        var progressDtos = new java.util.ArrayList<LessonProgressDTO>();
        for (LessonProgress progress : progresses) {
            progressDtos.add(toDto(progress));
        }
        return progressDtos;
    }

    @Override
    @Transactional
    public LessonProgressDTO recordProgress(UUID enrollmentId, UUID lessonId, String status) {
        var user = resolveAuthenticatedUser();
        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        if (!enrollment.getStudent().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not your enrollment");
        }
        var lesson = lessonRepository.findById(lessonId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        var enrollmentCourseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        var lessonCourseId = lesson.getSection() != null && lesson.getSection().getCourse() != null
                ? lesson.getSection().getCourse().getId() : null;
        if (enrollmentCourseId == null || !enrollmentCourseId.equals(lessonCourseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson does not belong to enrollment course");
        }

        var existing = lessonProgressRepository.findByEnrollment_IdAndLesson_Id(enrollmentId, lessonId);
        LessonProgress entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setStatus(status);
            entity.setCompletedAt("COMPLETED".equalsIgnoreCase(status) ? LocalDateTime.now() : null);
        } else {
            entity = LessonProgress.builder()
                    .enrollment(enrollment)
                    .lesson(lesson)
                    .student(enrollment.getStudent())
                    .status(status)
                    .completedAt("COMPLETED".equalsIgnoreCase(status) ? LocalDateTime.now() : null)
                    .build();
        }
        entity = lessonProgressRepository.save(entity);

        enrollment.setLastAccessedLessonId(lessonId.toString());
        var completedCount = lessonProgressRepository.findByEnrollment_IdOrderByCreatedAtAsc(enrollmentId).stream()
                .filter(p -> "COMPLETED".equalsIgnoreCase(p.getStatus()))
                .count();
        var totalLessons = lessonRepository.findBySection_Course_IdOrderBySection_OrderIndexAscOrderIndexAsc(enrollment.getCourse().getId()).size();
        enrollment.setCompletedLessonsCount((int) completedCount);
        enrollment.setProgress(totalLessons > 0 ? java.math.BigDecimal.valueOf(100.0 * completedCount / totalLessons) : java.math.BigDecimal.ZERO);
        if (totalLessons > 0 && completedCount >= totalLessons) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }
        enrollmentRepository.save(enrollment);

        return toDto(entity);
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

    private com.EGM.LMS.model.User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
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
