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
public class BookmarkDTO {

    private UUID id;
    private UserDTO user;
    private CourseDTO course;
    private LessonDTO lesson;
    private Integer timestamp;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
