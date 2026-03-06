package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstructorPayoutRequestDTO {
    private UUID id;
    private InstructorProfileDTO instructorProfile;
    private InstructorBankDetailDTO bankDetail;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
}
