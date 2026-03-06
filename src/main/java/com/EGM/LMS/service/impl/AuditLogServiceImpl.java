package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.AuditLogDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.AuditLog;
import com.EGM.LMS.repository.AuditLogRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Override
    public AuditLogDTO createAuditLog(AuditLogDTO auditLog) {
        return toDto(auditLogRepository.save(toEntity(auditLog)));
    }

    @Override
    public void logAction(String action, String targetType, String targetId, String changes, String ipAddress, String userAgent) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) return;
        var admin = userRepository.findByEmail(auth.getName()).orElse(null);
        if (admin == null) return;
        var dto = AuditLogDTO.builder()
                .admin(UserDTO.builder().id(admin.getId()).build())
                .action(action)
                .targetType(targetType)
                .targetId(targetId != null ? targetId.toString() : null)
                .changes(changes)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
        createAuditLog(dto);
    }

    @Override
    public List<AuditLogDTO> getAllAuditLogs() {
        var auditLogs = auditLogRepository.findAll();
        var auditLogDtos = new java.util.ArrayList<AuditLogDTO>();
        for (AuditLog auditLog : auditLogs) {
            auditLogDtos.add(toDto(auditLog));
        }
        return auditLogDtos;
    }

    @Override
    public AuditLogDTO getAuditLog(UUID auditLogId) {
        return toDto(auditLogRepository.findById(auditLogId).orElseThrow());
    }

    @Override
    public AuditLogDTO updateAuditLog(UUID auditLogId, AuditLogDTO auditLog) {
        auditLogRepository.findById(auditLogId).orElseThrow();
        var entity = toEntity(auditLog);
        entity.setId(auditLogId);
        return toDto(auditLogRepository.save(entity));
    }

    @Override
    public void deleteAuditLog(UUID auditLogId) {
        auditLogRepository.deleteById(auditLogId);
    }

    private AuditLog toEntity(AuditLogDTO auditLog) {
        var adminId = auditLog.getAdmin() != null ? auditLog.getAdmin().getId() : null;
        return AuditLog.builder()
                .admin(adminId != null ? userRepository.findById(adminId).orElse(null) : null)
                .action(auditLog.getAction())
                .targetType(auditLog.getTargetType())
                .targetId(auditLog.getTargetId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .changes(auditLog.getChanges())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .build();
    }

    private AuditLogDTO toDto(AuditLog auditLog) {
        return AuditLogDTO.builder()
                .id(auditLog.getId())
                .admin(auditLog.getAdmin() != null ? UserDTO.builder().id(auditLog.getAdmin().getId()).build() : null)
                .action(auditLog.getAction())
                .targetType(auditLog.getTargetType())
                .targetId(auditLog.getTargetId())
                .oldValue(auditLog.getOldValue())
                .newValue(auditLog.getNewValue())
                .changes(auditLog.getChanges())
                .ipAddress(auditLog.getIpAddress())
                .userAgent(auditLog.getUserAgent())
                .createdAt(auditLog.getCreatedAt())
                .updatedAt(auditLog.getUpdatedAt())
                .build();
    }
}
