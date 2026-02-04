package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadDTO {

    private UUID id;
    private UserDTO user;
    private LessonDTO lesson;
    private String videoQuality;
    private String fileUrl;
    private Long fileSize;
    private LocalDateTime expiresAt;
    private LocalDateTime downloadedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
