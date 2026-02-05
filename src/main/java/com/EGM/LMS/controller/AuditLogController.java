package com.EGM.LMS.controller;

import com.EGM.LMS.dto.AuditLogDTO;
import com.EGM.LMS.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit-logs")
public class AuditLogController {
    private final AuditLogService auditLogService;

    @PostMapping
    ResponseEntity<AuditLogDTO> createAuditLog(@RequestBody AuditLogDTO auditLogDto) {
        return ResponseEntity.ok(auditLogService.createAuditLog(auditLogDto));
    }

    @GetMapping
    ResponseEntity<List<AuditLogDTO>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    @GetMapping("/{auditLogId}")
    ResponseEntity<AuditLogDTO> getAuditLog(@PathVariable UUID auditLogId) {
        return ResponseEntity.ok(auditLogService.getAuditLog(auditLogId));
    }

    @PutMapping("/{auditLogId}")
    ResponseEntity<AuditLogDTO> updateAuditLog(@PathVariable UUID auditLogId, @RequestBody AuditLogDTO auditLogDto) {
        return ResponseEntity.ok(auditLogService.updateAuditLog(auditLogId, auditLogDto));
    }

    @DeleteMapping("/{auditLogId}")
    ResponseEntity<Void> deleteAuditLog(@PathVariable UUID auditLogId) {
        auditLogService.deleteAuditLog(auditLogId);
        return ResponseEntity.noContent().build();
    }
}
