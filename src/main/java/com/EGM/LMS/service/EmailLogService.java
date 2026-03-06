package com.EGM.LMS.service;

import com.EGM.LMS.dto.EmailLogDTO;

import java.util.List;
import java.util.UUID;

public interface EmailLogService {
    EmailLogDTO createEmailLog(EmailLogDTO emailLog);
    /** Record an email (and optionally send). Creates EmailLog; sending depends on app config. */
    void recordEmail(java.util.UUID recipientId, String emailAddress, String subject, String type, String status);
    List<EmailLogDTO> getAllEmailLogs();
    EmailLogDTO getEmailLog(UUID emailLogId);
    EmailLogDTO updateEmailLog(UUID emailLogId, EmailLogDTO emailLog);
    void deleteEmailLog(UUID emailLogId);
}
