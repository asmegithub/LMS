package com.EGM.LMS.controller;

import com.EGM.LMS.dto.PermissionDTO;
import com.EGM.LMS.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    @PostMapping
    ResponseEntity<PermissionDTO> createPermission(@RequestBody PermissionDTO permissionDto) {
        return ResponseEntity.ok(permissionService.createPermission(permissionDto));
    }

    @GetMapping
    ResponseEntity<List<PermissionDTO>> getAllPermissions() {
        return ResponseEntity.ok(permissionService.getAllPermissions());
    }

    @GetMapping("/{permissionId}")
    ResponseEntity<PermissionDTO> getPermission(@PathVariable UUID permissionId) {
        return ResponseEntity.ok(permissionService.getPermission(permissionId));
    }

    @PutMapping("/{permissionId}")
    ResponseEntity<PermissionDTO> updatePermission(@PathVariable UUID permissionId, @RequestBody PermissionDTO permissionDto) {
        return ResponseEntity.ok(permissionService.updatePermission(permissionId, permissionDto));
    }

    @DeleteMapping("/{permissionId}")
    ResponseEntity<Void> deletePermission(@PathVariable UUID permissionId) {
        permissionService.deletePermission(permissionId);
        return ResponseEntity.noContent().build();
    }
}
