package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PermissionDTO;
import com.EGM.LMS.dto.RoleDTO;
import com.EGM.LMS.dto.RolePermissionDTO;
import com.EGM.LMS.model.RolePermission;
import com.EGM.LMS.repository.PermissionRepository;
import com.EGM.LMS.repository.RolePermissionRepository;
import com.EGM.LMS.repository.RoleRepository;
import com.EGM.LMS.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public RolePermissionDTO createRolePermission(RolePermissionDTO rolePermission) {
        return toDto(rolePermissionRepository.save(toEntity(rolePermission)));
    }

    @Override
    public List<RolePermissionDTO> getAllRolePermissions() {
        var rolePermissions = rolePermissionRepository.findAll();
        var rolePermissionDtos = new java.util.ArrayList<RolePermissionDTO>();
        for (RolePermission rolePermission : rolePermissions) {
            rolePermissionDtos.add(toDto(rolePermission));
        }
        return rolePermissionDtos;
    }

    @Override
    public RolePermissionDTO getRolePermission(UUID rolePermissionId) {
        return toDto(rolePermissionRepository.findById(rolePermissionId).orElseThrow());
    }

    @Override
    public RolePermissionDTO updateRolePermission(UUID rolePermissionId, RolePermissionDTO rolePermission) {
        rolePermissionRepository.findById(rolePermissionId).orElseThrow();
        var entity = toEntity(rolePermission);
        entity.setId(rolePermissionId);
        return toDto(rolePermissionRepository.save(entity));
    }

    @Override
    public void deleteRolePermission(UUID rolePermissionId) {
        rolePermissionRepository.deleteById(rolePermissionId);
    }

    private RolePermission toEntity(RolePermissionDTO rolePermission) {
        var roleId = rolePermission.getRole() != null ? rolePermission.getRole().getId() : null;
        var permissionId = rolePermission.getPermission() != null ? rolePermission.getPermission().getId() : null;
        return RolePermission.builder()
                .role(roleId != null ? roleRepository.findById(roleId).orElse(null) : null)
                .permission(permissionId != null ? permissionRepository.findById(permissionId).orElse(null) : null)
                .build();
    }

    private RolePermissionDTO toDto(RolePermission rolePermission) {
        return RolePermissionDTO.builder()
                .id(rolePermission.getId())
                .role(rolePermission.getRole() != null ? RoleDTO.builder().id(rolePermission.getRole().getId()).build() : null)
                .permission(rolePermission.getPermission() != null ? PermissionDTO.builder().id(rolePermission.getPermission().getId()).build() : null)
                .createdAt(rolePermission.getCreatedAt())
                .updatedAt(rolePermission.getUpdatedAt())
                .build();
    }
}
