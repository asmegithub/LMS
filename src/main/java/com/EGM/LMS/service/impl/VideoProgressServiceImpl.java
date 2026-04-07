package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.VideoProgressDTO;
import com.EGM.LMS.model.LessonProgress;
import com.EGM.LMS.model.VideoProgress;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.LessonProgressRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.VideoProgressRepository;
import com.EGM.LMS.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VideoProgressServiceImpl implements VideoProgressService {
    private final VideoProgressRepository videoProgressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final LessonProgressRepository lessonProgressRepository;

    @Override
    public VideoProgressDTO createVideoProgress(VideoProgressDTO videoProgress) {
        return toDto(videoProgressRepository.save(toEntity(videoProgress)));
    }

    @Override
    public Optional<VideoProgressDTO> getByEnrollmentAndLesson(UUID enrollmentId, UUID lessonId) {
        var user = resolveAuthenticatedUser();
        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        validateEnrollmentLessonOwnership(enrollment, lesson, user);
        return videoProgressRepository.findByEnrollment_IdAndLesson_Id(enrollmentId, lessonId).map(this::toDto);
    }

    @Override
    public VideoProgressDTO emptyProgressDto() {
        return VideoProgressDTO.builder()
                .lastWatchedPosition(0)
                .watchedDuration(0)
                .totalDuration(0)
                .build();
    }

    @Override
    @Transactional
    public VideoProgressDTO upsertProgress(UUID enrollmentId, UUID lessonId, Integer lastWatchedPosition, Integer watchedDuration, Integer totalDuration) {
        return doUpsert(enrollmentId, lessonId, lastWatchedPosition, watchedDuration, totalDuration);
    }

    private VideoProgressDTO doUpsert(UUID enrollmentId, UUID lessonId, Integer lastWatchedPosition, Integer watchedDuration, Integer totalDuration) {
        var user = resolveAuthenticatedUser();
        var enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found"));
        var lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lesson not found"));
        validateEnrollmentLessonOwnership(enrollment, lesson, user);
        var student = enrollment.getStudent();
        var existing = videoProgressRepository.findByEnrollment_IdAndLesson_Id(enrollmentId, lessonId);
        VideoProgress entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setLastWatchedPosition(lastWatchedPosition != null ? lastWatchedPosition : entity.getLastWatchedPosition());
            entity.setWatchedDuration(watchedDuration != null ? watchedDuration : entity.getWatchedDuration());
            entity.setTotalDuration(totalDuration != null ? totalDuration : entity.getTotalDuration());
        } else {
            entity = VideoProgress.builder()
                    .enrollment(enrollment)
                    .lesson(lesson)
                    .student(student)
                    .lastWatchedPosition(lastWatchedPosition != null ? lastWatchedPosition : 0)
                    .watchedDuration(watchedDuration != null ? watchedDuration : 0)
                    .totalDuration(totalDuration != null ? totalDuration : 0)
                    .build();
        }
        if (entity.getTotalDuration() != null && entity.getTotalDuration() > 0 && entity.getWatchedDuration() != null) {
            entity.setWatchPercentage(BigDecimal.valueOf(100.0 * entity.getWatchedDuration() / entity.getTotalDuration()).setScale(2, RoundingMode.HALF_UP));
        }
        var saved = videoProgressRepository.save(entity);

        enrollment.setLastAccessedLessonId(lessonId.toString());
        enrollmentRepository.save(enrollment);

        // Update LessonProgress + enrollment progress based on the latest watched state.
        // This gives: (1) IN_PROGRESS vs COMPLETED distinction, and (2) a real-time course percentage.
        updateLessonProgressFromVideo(enrollmentId, enrollment, lesson, saved, lastWatchedPosition, watchedDuration, totalDuration);
        updateEnrollmentProgress(enrollmentId, enrollment.getCourse().getId());

        return toDto(saved);
    }

    private void updateLessonProgressFromVideo(
            UUID enrollmentId,
            com.EGM.LMS.model.Enrollment enrollment,
            com.EGM.LMS.model.Lesson lesson,
            VideoProgress saved,
            Integer lastWatchedPosition,
            Integer watchedDuration,
            Integer totalDuration
    ) {
        if (!"VIDEO".equalsIgnoreCase(lesson.getType())) return;

        var progressOpt = lessonProgressRepository.findByEnrollment_IdAndLesson_Id(enrollmentId, saved.getLesson().getId());
        var progress = progressOpt.orElseGet(() -> LessonProgress.builder()
                .enrollment(enrollment)
                .lesson(lesson)
                .student(enrollment.getStudent())
                .status("NOT_STARTED")
                .build());

        int currentPos = saved.getLastWatchedPosition() != null ? saved.getLastWatchedPosition() : (lastWatchedPosition != null ? lastWatchedPosition : 0);
        int total = saved.getTotalDuration() != null ? saved.getTotalDuration() : (totalDuration != null ? totalDuration : 0);
        int watched = saved.getWatchedDuration() != null ? saved.getWatchedDuration() : (watchedDuration != null ? watchedDuration : 0);

        // Videos often finish with a few seconds of rounding differences.
        // Treat "finished" when we're within a small tolerance window.
        int toleranceSeconds = 2;
        boolean isActuallyFinished = total > 0 && (watched >= total - toleranceSeconds || currentPos >= total - toleranceSeconds);
        boolean hasStarted = watched > 0 || currentPos > 0;

        if (isActuallyFinished) {
            progress.setStatus("COMPLETED");
            progress.setCompletedAt(LocalDateTime.now());
        } else if (hasStarted && !"COMPLETED".equalsIgnoreCase(progress.getStatus())) {
            progress.setStatus("IN_PROGRESS");
            progress.setCompletedAt(null);
        }

        lessonProgressRepository.save(progress);
    }

    private void updateEnrollmentProgress(UUID enrollmentId, UUID courseId) {
        var courseLessons = lessonRepository.findBySection_Course_IdOrderBySection_OrderIndexAscOrderIndexAsc(courseId);
        if (courseLessons == null || courseLessons.isEmpty()) return;

        var lessonProgresses = lessonProgressRepository.findByEnrollment_IdOrderByCreatedAtAsc(enrollmentId);
        var statusByLessonId = new HashMap<UUID, String>();
        for (LessonProgress p : lessonProgresses) {
            if (p.getLesson() != null && p.getLesson().getId() != null) {
                statusByLessonId.put(p.getLesson().getId(), p.getStatus());
            }
        }

        var videoProgresses = videoProgressRepository.findByEnrollment_Id(enrollmentId);
        var videoByLessonId = new HashMap<UUID, VideoProgress>();
        for (VideoProgress vp : videoProgresses) {
            if (vp.getLesson() != null && vp.getLesson().getId() != null) {
                videoByLessonId.put(vp.getLesson().getId(), vp);
            }
        }

        int totalLessons = courseLessons.size();
        int completedLessonsCount = 0;
        double totalScore = 0.0;

        for (var lesson : courseLessons) {
            var status = statusByLessonId.get(lesson.getId());
            if (status != null && "COMPLETED".equalsIgnoreCase(status)) {
                completedLessonsCount++;
                totalScore += 1.0;
                continue;
            }

            if ("VIDEO".equalsIgnoreCase(lesson.getType())) {
                var vp = videoByLessonId.get(lesson.getId());
                if (vp != null && vp.getTotalDuration() != null && vp.getTotalDuration() > 0 && vp.getWatchedDuration() != null) {
                    double ratio = ((double) vp.getWatchedDuration()) / ((double) vp.getTotalDuration());
                    if (ratio < 0) ratio = 0;
                    if (ratio > 1) ratio = 1;
                    totalScore += ratio;
                } else {
                    totalScore += 0.0;
                }
                continue;
            }

            // TEXT/DOCUMENT/QUIZ: they count only when COMPLETED via LessonProgress.
            totalScore += 0.0;
        }

        var enrollmentOpt = enrollmentRepository.findById(enrollmentId);
        if (enrollmentOpt.isEmpty()) return;
        var enrollment = enrollmentOpt.get();

        enrollment.setCompletedLessonsCount(completedLessonsCount);
        enrollment.setProgress(java.math.BigDecimal.valueOf(100.0 * totalScore / totalLessons));
        if (completedLessonsCount >= totalLessons) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        } else {
            // Don't clear completedAt/status if it was previously completed; only mark completion once.
            // (If you need "un-complete" behavior, we can add it.)
        }

        enrollmentRepository.save(enrollment);
    }

    @Override
    public List<VideoProgressDTO> getAllVideoProgresses() {
        var user = resolveAuthenticatedUser();
        var progresses = videoProgressRepository.findAll();
        var progressDtos = new java.util.ArrayList<VideoProgressDTO>();
        for (VideoProgress progress : progresses) {
            if (canAccessProgress(progress, user)) {
                progressDtos.add(toDto(progress));
            }
        }
        return progressDtos;
    }

    @Override
    public VideoProgressDTO getVideoProgress(UUID videoProgressId) {
        var user = resolveAuthenticatedUser();
        var progress = videoProgressRepository.findById(videoProgressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video progress not found"));
        if (!canAccessProgress(progress, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this progress");
        }
        return toDto(progress);
    }

    @Override
    public VideoProgressDTO updateVideoProgress(UUID videoProgressId, VideoProgressDTO videoProgress) {
        var existing = videoProgressRepository.findById(videoProgressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video progress not found"));
        var user = resolveAuthenticatedUser();
        if (!canAccessProgress(existing, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this progress");
        }
        var entity = toEntity(videoProgress);
        entity.setId(videoProgressId);
        return toDto(videoProgressRepository.save(entity));
    }

    @Override
    public void deleteVideoProgress(UUID videoProgressId) {
        var existing = videoProgressRepository.findById(videoProgressId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Video progress not found"));
        var user = resolveAuthenticatedUser();
        if (!canAccessProgress(existing, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this progress");
        }
        videoProgressRepository.deleteById(videoProgressId);
    }

    private void validateEnrollmentLessonOwnership(com.EGM.LMS.model.Enrollment enrollment, com.EGM.LMS.model.Lesson lesson, com.EGM.LMS.model.User user) {
        var enrollmentCourseId = enrollment.getCourse() != null ? enrollment.getCourse().getId() : null;
        var lessonCourseId = lesson.getSection() != null && lesson.getSection().getCourse() != null
                ? lesson.getSection().getCourse().getId() : null;
        if (enrollmentCourseId == null || !enrollmentCourseId.equals(lessonCourseId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lesson does not belong to enrollment course");
        }
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        var isAdmin = "ADMIN".equalsIgnoreCase(user.getRole());
        var isOwner = enrollment.getStudent() != null && user.getId().equals(enrollment.getStudent().getId());
        var isCourseInstructor = enrollment.getCourse() != null
                && enrollment.getCourse().getInstructor() != null
                && enrollment.getCourse().getInstructor().getUser() != null
                && user.getId().equals(enrollment.getCourse().getInstructor().getUser().getId());
        if (!isAdmin && !isOwner && !isCourseInstructor) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this enrollment");
        }
    }

    private boolean canAccessProgress(VideoProgress progress, com.EGM.LMS.model.User user) {
        if (progress == null || user == null || user.getId() == null) return false;
        if ("ADMIN".equalsIgnoreCase(user.getRole())) return true;
        var studentId = progress.getStudent() != null ? progress.getStudent().getId() : null;
        if (studentId != null && studentId.equals(user.getId())) return true;
        return progress.getEnrollment() != null
                && progress.getEnrollment().getCourse() != null
                && progress.getEnrollment().getCourse().getInstructor() != null
                && progress.getEnrollment().getCourse().getInstructor().getUser() != null
                && user.getId().equals(progress.getEnrollment().getCourse().getInstructor().getUser().getId());
    }

    private com.EGM.LMS.model.User resolveAuthenticatedUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank() || "anonymousUser".equals(auth.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private VideoProgress toEntity(VideoProgressDTO videoProgress) {
        var enrollmentId = videoProgress.getEnrollment() != null ? videoProgress.getEnrollment().getId() : null;
        var lessonId = videoProgress.getLesson() != null ? videoProgress.getLesson().getId() : null;
        var studentId = videoProgress.getStudent() != null ? videoProgress.getStudent().getId() : null;
        return VideoProgress.builder()
                .enrollment(enrollmentId != null ? enrollmentRepository.findById(enrollmentId).orElse(null) : null)
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .student(studentId != null ? userRepository.findById(studentId).orElse(null) : null)
                .watchedDuration(videoProgress.getWatchedDuration())
                .totalDuration(videoProgress.getTotalDuration())
                .watchPercentage(videoProgress.getWatchPercentage())
                .lastWatchedPosition(videoProgress.getLastWatchedPosition())
                .playbackSpeed(videoProgress.getPlaybackSpeed())
                .build();
    }

    private VideoProgressDTO toDto(VideoProgress videoProgress) {
        return VideoProgressDTO.builder()
                .id(videoProgress.getId())
                .enrollment(videoProgress.getEnrollment() != null ? EnrollmentDTO.builder().id(videoProgress.getEnrollment().getId()).build() : null)
                .lesson(videoProgress.getLesson() != null ? LessonDTO.builder().id(videoProgress.getLesson().getId()).build() : null)
                .student(videoProgress.getStudent() != null ? UserDTO.builder().id(videoProgress.getStudent().getId()).build() : null)
                .watchedDuration(videoProgress.getWatchedDuration())
                .totalDuration(videoProgress.getTotalDuration())
                .watchPercentage(videoProgress.getWatchPercentage())
                .lastWatchedPosition(videoProgress.getLastWatchedPosition())
                .playbackSpeed(videoProgress.getPlaybackSpeed())
                .createdAt(videoProgress.getCreatedAt())
                .updatedAt(videoProgress.getUpdatedAt())
                .build();
    }
}
