package com.EGM.LMS.service;

import com.EGM.LMS.dto.VideoProgressDTO;

import java.util.List;
import java.util.UUID;

public interface VideoProgressService {
    VideoProgressDTO createVideoProgress(VideoProgressDTO videoProgress);
    java.util.Optional<VideoProgressDTO> getByEnrollmentAndLesson(UUID enrollmentId, UUID lessonId);
    VideoProgressDTO emptyProgressDto();
    VideoProgressDTO upsertProgress(UUID enrollmentId, UUID lessonId, Integer lastWatchedPosition, Integer watchedDuration, Integer totalDuration);
    List<VideoProgressDTO> getAllVideoProgresses();
    VideoProgressDTO getVideoProgress(UUID videoProgressId);
    VideoProgressDTO updateVideoProgress(UUID videoProgressId, VideoProgressDTO videoProgress);
    void deleteVideoProgress(UUID videoProgressId);
}
