package com.EGM.LMS.service;

import com.EGM.LMS.dto.VideoProgressDTO;

import java.util.List;
import java.util.UUID;

public interface VideoProgressService {
    VideoProgressDTO createVideoProgress(VideoProgressDTO videoProgress);
    List<VideoProgressDTO> getAllVideoProgresses();
    VideoProgressDTO getVideoProgress(UUID videoProgressId);
    VideoProgressDTO updateVideoProgress(UUID videoProgressId, VideoProgressDTO videoProgress);
    void deleteVideoProgress(UUID videoProgressId);
}
