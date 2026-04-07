package com.EGM.LMS.controller;

import com.EGM.LMS.dto.RoleDTO;
import com.EGM.LMS.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/roles")
@PreAuthorize("hasAuthority('roles.manage')")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    ResponseEntity<RoleDTO> createRole(@RequestBody RoleDTO roleDto) {
        return ResponseEntity.ok(roleService.createRole(roleDto));
    }

    @GetMapping
    ResponseEntity<List<RoleDTO>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    @GetMapping("/{roleId}")
    ResponseEntity<RoleDTO> getRole(@PathVariable UUID roleId) {
        return ResponseEntity.ok(roleService.getRole(roleId));
    }

    @PutMapping("/{roleId}")
    ResponseEntity<RoleDTO> updateRole(@PathVariable UUID roleId, @RequestBody RoleDTO roleDto) {
        return ResponseEntity.ok(roleService.updateRole(roleId, roleDto));
    }

    @DeleteMapping("/{roleId}")
    ResponseEntity<Void> deleteRole(@PathVariable UUID roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.noContent().build();
    }
}
