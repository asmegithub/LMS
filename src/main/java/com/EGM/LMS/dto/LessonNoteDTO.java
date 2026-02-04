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
public class LessonNoteDTO {

    private UUID id;
    private LessonDTO lesson;
    private UserDTO student;
    private String content;
    private int timestamp;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
