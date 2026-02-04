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
public class RefundDTO {

    private UUID id;
    private PaymentDTO payment;
    private BigDecimal amount;
    private String reason;
    private String status;
    private String processedBy;
    private LocalDateTime processedAt;
    private String gatewayRef;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
