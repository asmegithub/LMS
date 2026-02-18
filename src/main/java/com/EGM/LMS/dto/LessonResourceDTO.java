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
public class LessonResourceDTO {

    private UUID id;
    private LessonDTO lesson;
    private String title;
    private String titleAm;
    private String titleOm;
    private String titleGz;
    private String type;
    private String url;
    private Integer fileSize;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
