package com.EGM.LMS.controller;

import com.EGM.LMS.dto.SystemSettingDTO;
import com.EGM.LMS.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system-settings")
public class SystemSettingController {
    private final SystemSettingService systemSettingService;

    @GetMapping("/public")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<SystemSettingDTO>> getPublicSystemSettings() {
        return ResponseEntity.ok(systemSettingService.getPublicSystemSettings());
    }

    @PostMapping("/upsert")
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<SystemSettingDTO> upsertSystemSetting(@RequestBody SystemSettingDTO systemSettingDto) {
        return ResponseEntity.ok(systemSettingService.upsertSystemSetting(systemSettingDto));
    }

    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<List<SystemSettingDTO>> batchUpsertSystemSettings(@RequestBody List<SystemSettingDTO> dtos) {
        return ResponseEntity.ok(systemSettingService.batchUpsertSystemSettings(dtos));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<SystemSettingDTO> createSystemSetting(@RequestBody SystemSettingDTO systemSettingDto) {
        return ResponseEntity.ok(systemSettingService.createSystemSetting(systemSettingDto));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<List<SystemSettingDTO>> getAllSystemSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSystemSettings());
    }

    @GetMapping("/{systemSettingId}")
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<SystemSettingDTO> getSystemSetting(@PathVariable UUID systemSettingId) {
        return ResponseEntity.ok(systemSettingService.getSystemSetting(systemSettingId));
    }

    @PutMapping("/{systemSettingId}")
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<SystemSettingDTO> updateSystemSetting(@PathVariable UUID systemSettingId,
            @RequestBody SystemSettingDTO systemSettingDto) {
        return ResponseEntity.ok(systemSettingService.updateSystemSetting(systemSettingId, systemSettingDto));
    }

    @DeleteMapping("/{systemSettingId}")
    @PreAuthorize("hasAuthority('system.settings.manage')")
    public ResponseEntity<Void> deleteSystemSetting(@PathVariable UUID systemSettingId) {
        systemSettingService.deleteSystemSetting(systemSettingId);
        return ResponseEntity.noContent().build();
    }
}
