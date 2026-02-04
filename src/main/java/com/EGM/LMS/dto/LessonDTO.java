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
public class LessonDTO {

    private UUID id;
    private CourseSectionDTO section;
    private String title;
    private String titleAm;
    private String titleOm;
    private String titleGz;
    private String type;
    private String videoUrl;
    private String videoUrl240p;
    private String videoUrl360p;
    private String videoUrl720p;
    private String encryptedVideoUrl;
    private Integer duration;
    private String documentUrl;
    private String documentType;
    private String content;
    private Integer orderIndex;
    private Boolean isFree;
    private Boolean isDownloadable;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
