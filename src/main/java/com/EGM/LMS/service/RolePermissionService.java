package com.EGM.LMS.service;

import com.EGM.LMS.dto.RolePermissionDTO;

import java.util.List;
import java.util.UUID;

public interface RolePermissionService {
    RolePermissionDTO createRolePermission(RolePermissionDTO rolePermission);
    List<RolePermissionDTO> getAllRolePermissions();
    RolePermissionDTO getRolePermission(UUID rolePermissionId);
    RolePermissionDTO updateRolePermission(UUID rolePermissionId, RolePermissionDTO rolePermission);
    void deleteRolePermission(UUID rolePermissionId);
}
