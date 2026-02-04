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
public class PermissionDTO {

    private UUID id;
    private String name;
    private String displayName;
    private String module;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
