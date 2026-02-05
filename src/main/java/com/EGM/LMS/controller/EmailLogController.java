package com.EGM.LMS.controller;

import com.EGM.LMS.dto.EmailLogDTO;
import com.EGM.LMS.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email-logs")
public class EmailLogController {
    private final EmailLogService emailLogService;

    @PostMapping
    ResponseEntity<EmailLogDTO> createEmailLog(@RequestBody EmailLogDTO emailLogDto) {
        return ResponseEntity.ok(emailLogService.createEmailLog(emailLogDto));
    }

    @GetMapping
    ResponseEntity<List<EmailLogDTO>> getAllEmailLogs() {
        return ResponseEntity.ok(emailLogService.getAllEmailLogs());
    }

    @GetMapping("/{emailLogId}")
    ResponseEntity<EmailLogDTO> getEmailLog(@PathVariable UUID emailLogId) {
        return ResponseEntity.ok(emailLogService.getEmailLog(emailLogId));
    }

    @PutMapping("/{emailLogId}")
    ResponseEntity<EmailLogDTO> updateEmailLog(@PathVariable UUID emailLogId, @RequestBody EmailLogDTO emailLogDto) {
        return ResponseEntity.ok(emailLogService.updateEmailLog(emailLogId, emailLogDto));
    }

    @DeleteMapping("/{emailLogId}")
    ResponseEntity<Void> deleteEmailLog(@PathVariable UUID emailLogId) {
        emailLogService.deleteEmailLog(emailLogId);
        return ResponseEntity.noContent().build();
    }
}
