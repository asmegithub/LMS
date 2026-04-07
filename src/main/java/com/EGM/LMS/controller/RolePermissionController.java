package com.EGM.LMS.controller;

import com.EGM.LMS.dto.RolePermissionDTO;
import com.EGM.LMS.service.RolePermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role-permissions")
@PreAuthorize("hasAuthority('role-permissions.manage')")
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @PostMapping
    ResponseEntity<RolePermissionDTO> createRolePermission(@RequestBody RolePermissionDTO rolePermissionDto) {
        return ResponseEntity.ok(rolePermissionService.createRolePermission(rolePermissionDto));
    }

    @GetMapping
    ResponseEntity<List<RolePermissionDTO>> getAllRolePermissions() {
        return ResponseEntity.ok(rolePermissionService.getAllRolePermissions());
    }

    @GetMapping("/{rolePermissionId}")
    ResponseEntity<RolePermissionDTO> getRolePermission(@PathVariable UUID rolePermissionId) {
        return ResponseEntity.ok(rolePermissionService.getRolePermission(rolePermissionId));
    }

    @PutMapping("/{rolePermissionId}")
    ResponseEntity<RolePermissionDTO> updateRolePermission(@PathVariable UUID rolePermissionId,
            @RequestBody RolePermissionDTO rolePermissionDto) {
        return ResponseEntity.ok(rolePermissionService.updateRolePermission(rolePermissionId, rolePermissionDto));
    }

    @DeleteMapping("/{rolePermissionId}")
    ResponseEntity<Void> deleteRolePermission(@PathVariable UUID rolePermissionId) {
        rolePermissionService.deleteRolePermission(rolePermissionId);
        return ResponseEntity.noContent().build();
    }
}
