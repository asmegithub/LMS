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
public class CourseCategoryDTO {

    private UUID id;
    private String name;
    private String nameAm;
    private String nameOm;
    private String nameGz;
    private String slug;
    private String description;
    private String icon;
    private String parentId;
    private int orderIndex;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
