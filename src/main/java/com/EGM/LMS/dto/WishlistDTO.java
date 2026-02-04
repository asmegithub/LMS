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
public class WishlistDTO {

    private UUID id;
    private UserDTO user;
    private CourseDTO course;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
