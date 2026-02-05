package com.EGM.LMS.service;

import com.EGM.LMS.dto.RoleDTO;

import java.util.List;
import java.util.UUID;

public interface RoleService {
    RoleDTO createRole(RoleDTO role);
    List<RoleDTO> getAllRoles();
    RoleDTO getRole(UUID roleId);
    RoleDTO updateRole(UUID roleId, RoleDTO role);
    void deleteRole(UUID roleId);
}
