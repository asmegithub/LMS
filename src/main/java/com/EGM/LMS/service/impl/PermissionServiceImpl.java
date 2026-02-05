package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.PermissionDTO;
import com.EGM.LMS.model.Permission;
import com.EGM.LMS.repository.PermissionRepository;
import com.EGM.LMS.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public PermissionDTO createPermission(PermissionDTO permission) {
        return toDto(permissionRepository.save(toEntity(permission)));
    }

    @Override
    public List<PermissionDTO> getAllPermissions() {
        var permissions = permissionRepository.findAll();
        var permissionDtos = new java.util.ArrayList<PermissionDTO>();
        for (Permission permission : permissions) {
            permissionDtos.add(toDto(permission));
        }
        return permissionDtos;
    }

    @Override
    public PermissionDTO getPermission(UUID permissionId) {
        return toDto(permissionRepository.findById(permissionId).orElseThrow());
    }

    @Override
    public PermissionDTO updatePermission(UUID permissionId, PermissionDTO permission) {
        permissionRepository.findById(permissionId).orElseThrow();
        var entity = toEntity(permission);
        entity.setId(permissionId);
        return toDto(permissionRepository.save(entity));
    }

    @Override
    public void deletePermission(UUID permissionId) {
        permissionRepository.deleteById(permissionId);
    }

    private Permission toEntity(PermissionDTO permission) {
        return Permission.builder()
                .name(permission.getName())
                .displayName(permission.getDisplayName())
                .module(permission.getModule())
                .description(permission.getDescription())
                .build();
    }

    private PermissionDTO toDto(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .displayName(permission.getDisplayName())
                .module(permission.getModule())
                .description(permission.getDescription())
                .createdAt(permission.getCreatedAt())
                .updatedAt(permission.getUpdatedAt())
                .build();
    }
}
