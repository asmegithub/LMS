package com.EGM.LMS.service;

import com.EGM.LMS.dto.AuditLogDTO;

import java.util.List;
import java.util.UUID;

public interface AuditLogService {
    AuditLogDTO createAuditLog(AuditLogDTO auditLog);
    List<AuditLogDTO> getAllAuditLogs();
    AuditLogDTO getAuditLog(UUID auditLogId);
    AuditLogDTO updateAuditLog(UUID auditLogId, AuditLogDTO auditLog);
    void deleteAuditLog(UUID auditLogId);
}
