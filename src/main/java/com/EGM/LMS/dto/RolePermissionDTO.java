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
public class RolePermissionDTO {

    private UUID id;
    private RoleDTO role;
    private PermissionDTO permission;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
