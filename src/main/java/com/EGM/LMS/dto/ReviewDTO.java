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
public class ReviewDTO {

    private UUID id;
    private CourseDTO course;
    private UserDTO student;
    private int rating;
    private String title;
    private String content;
    private boolean visible;
    private int helpfulCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
