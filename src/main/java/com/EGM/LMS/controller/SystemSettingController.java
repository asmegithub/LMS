package com.EGM.LMS.controller;

import com.EGM.LMS.dto.SystemSettingDTO;
import com.EGM.LMS.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/system-settings")
public class SystemSettingController {
    private final SystemSettingService systemSettingService;

    @PostMapping
    ResponseEntity<SystemSettingDTO> createSystemSetting(@RequestBody SystemSettingDTO systemSettingDto) {
        return ResponseEntity.ok(systemSettingService.createSystemSetting(systemSettingDto));
    }

    @GetMapping
    ResponseEntity<List<SystemSettingDTO>> getAllSystemSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSystemSettings());
    }

    @GetMapping("/{systemSettingId}")
    ResponseEntity<SystemSettingDTO> getSystemSetting(@PathVariable UUID systemSettingId) {
        return ResponseEntity.ok(systemSettingService.getSystemSetting(systemSettingId));
    }

    @PutMapping("/{systemSettingId}")
    ResponseEntity<SystemSettingDTO> updateSystemSetting(@PathVariable UUID systemSettingId, @RequestBody SystemSettingDTO systemSettingDto) {
        return ResponseEntity.ok(systemSettingService.updateSystemSetting(systemSettingId, systemSettingDto));
    }

    @DeleteMapping("/{systemSettingId}")
    ResponseEntity<Void> deleteSystemSetting(@PathVariable UUID systemSettingId) {
        systemSettingService.deleteSystemSetting(systemSettingId);
        return ResponseEntity.noContent().build();
    }
}
