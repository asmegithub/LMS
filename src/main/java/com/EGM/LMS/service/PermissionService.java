package com.EGM.LMS.service;

import com.EGM.LMS.dto.PermissionDTO;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    PermissionDTO createPermission(PermissionDTO permission);
    List<PermissionDTO> getAllPermissions();
    PermissionDTO getPermission(UUID permissionId);
    PermissionDTO updatePermission(UUID permissionId, PermissionDTO permission);
    void deletePermission(UUID permissionId);
}
