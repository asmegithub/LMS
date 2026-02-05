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

import java.util.List;
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
