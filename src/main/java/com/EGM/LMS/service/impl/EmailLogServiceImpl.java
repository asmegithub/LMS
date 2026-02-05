package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.EmailLogDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.EmailLog;
import com.EGM.LMS.repository.EmailLogRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.EmailLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailLogServiceImpl implements EmailLogService {
    private final EmailLogRepository emailLogRepository;
    private final UserRepository userRepository;

    @Override
    public EmailLogDTO createEmailLog(EmailLogDTO emailLog) {
        return toDto(emailLogRepository.save(toEntity(emailLog)));
    }

    @Override
    public List<EmailLogDTO> getAllEmailLogs() {
        var emailLogs = emailLogRepository.findAll();
        var emailLogDtos = new java.util.ArrayList<EmailLogDTO>();
        for (EmailLog emailLog : emailLogs) {
            emailLogDtos.add(toDto(emailLog));
        }
        return emailLogDtos;
    }

    @Override
    public EmailLogDTO getEmailLog(UUID emailLogId) {
        return toDto(emailLogRepository.findById(emailLogId).orElseThrow());
    }

    @Override
    public EmailLogDTO updateEmailLog(UUID emailLogId, EmailLogDTO emailLog) {
        emailLogRepository.findById(emailLogId).orElseThrow();
        var entity = toEntity(emailLog);
        entity.setId(emailLogId);
        return toDto(emailLogRepository.save(entity));
    }

    @Override
    public void deleteEmailLog(UUID emailLogId) {
        emailLogRepository.deleteById(emailLogId);
    }

    private EmailLog toEntity(EmailLogDTO emailLog) {
        var recipientId = emailLog.getRecipient() != null ? emailLog.getRecipient().getId() : null;
        return EmailLog.builder()
                .recipient(recipientId != null ? userRepository.findById(recipientId).orElse(null) : null)
                .email(emailLog.getEmail())
                .subject(emailLog.getSubject())
                .type(emailLog.getType())
                .status(emailLog.getStatus())
                .errorMessage(emailLog.getErrorMessage())
                .build();
    }

    private EmailLogDTO toDto(EmailLog emailLog) {
        return EmailLogDTO.builder()
                .id(emailLog.getId())
                .recipient(emailLog.getRecipient() != null ? UserDTO.builder().id(emailLog.getRecipient().getId()).build() : null)
                .email(emailLog.getEmail())
                .subject(emailLog.getSubject())
                .type(emailLog.getType())
                .status(emailLog.getStatus())
                .errorMessage(emailLog.getErrorMessage())
                .createdAt(emailLog.getCreatedAt())
                .updatedAt(emailLog.getUpdatedAt())
                .build();
    }
}
