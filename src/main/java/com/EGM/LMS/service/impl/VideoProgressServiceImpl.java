package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.EnrollmentDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.dto.VideoProgressDTO;
import com.EGM.LMS.model.VideoProgress;
import com.EGM.LMS.repository.EnrollmentRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.repository.VideoProgressRepository;
import com.EGM.LMS.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Override
    public VideoProgressDTO createVideoProgress(VideoProgressDTO videoProgress) {
        return toDto(videoProgressRepository.save(toEntity(videoProgress)));
    }

    @Override
    public Optional<VideoProgressDTO> getByEnrollmentAndLesson(UUID enrollmentId, UUID lessonId) {
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
    public VideoProgressDTO upsertProgress(UUID enrollmentId, UUID lessonId, Integer lastWatchedPosition, Integer watchedDuration, Integer totalDuration) {
        return doUpsert(enrollmentId, lessonId, lastWatchedPosition, watchedDuration, totalDuration);
    }

    private VideoProgressDTO doUpsert(UUID enrollmentId, UUID lessonId, Integer lastWatchedPosition, Integer watchedDuration, Integer totalDuration) {
        var enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow();
        var lesson = lessonRepository.findById(lessonId).orElseThrow();
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
        return toDto(videoProgressRepository.save(entity));
    }

    @Override
    public List<VideoProgressDTO> getAllVideoProgresses() {
        var progresses = videoProgressRepository.findAll();
        var progressDtos = new java.util.ArrayList<VideoProgressDTO>();
        for (VideoProgress progress : progresses) {
            progressDtos.add(toDto(progress));
        }
        return progressDtos;
    }

    @Override
    public VideoProgressDTO getVideoProgress(UUID videoProgressId) {
        return toDto(videoProgressRepository.findById(videoProgressId).orElseThrow());
    }

    @Override
    public VideoProgressDTO updateVideoProgress(UUID videoProgressId, VideoProgressDTO videoProgress) {
        videoProgressRepository.findById(videoProgressId).orElseThrow();
        var entity = toEntity(videoProgress);
        entity.setId(videoProgressId);
        return toDto(videoProgressRepository.save(entity));
    }

    @Override
    public void deleteVideoProgress(UUID videoProgressId) {
        videoProgressRepository.deleteById(videoProgressId);
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
