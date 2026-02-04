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
public class PayoutDTO {

    private UUID id;
    private InstructorProfileDTO instructorProfile;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paymentMethod;
    private String paymentDetails;
    private String referenceId;
    private String failureReason;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
