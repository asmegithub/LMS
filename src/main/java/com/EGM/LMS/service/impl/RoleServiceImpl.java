package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.RoleDTO;
import com.EGM.LMS.model.Role;
import com.EGM.LMS.repository.RoleRepository;
import com.EGM.LMS.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    @Override
    public RoleDTO createRole(RoleDTO role) {
        return toDto(roleRepository.save(toEntity(role)));
    }

    @Override
    public List<RoleDTO> getAllRoles() {
        var roles = roleRepository.findAll();
        var roleDtos = new java.util.ArrayList<RoleDTO>();
        for (Role role : roles) {
            roleDtos.add(toDto(role));
        }
        return roleDtos;
    }

    @Override
    public RoleDTO getRole(UUID roleId) {
        return toDto(roleRepository.findById(roleId).orElseThrow());
    }

    @Override
    public RoleDTO updateRole(UUID roleId, RoleDTO role) {
        roleRepository.findById(roleId).orElseThrow();
        var entity = toEntity(role);
        entity.setId(roleId);
        return toDto(roleRepository.save(entity));
    }

    @Override
    public void deleteRole(UUID roleId) {
        roleRepository.deleteById(roleId);
    }

    private Role toEntity(RoleDTO role) {
        return Role.builder()
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .build();
    }

    private RoleDTO toDto(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .displayName(role.getDisplayName())
                .description(role.getDescription())
                .isSystem(role.getIsSystem())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }
}
