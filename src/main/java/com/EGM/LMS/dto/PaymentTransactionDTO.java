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
public class PaymentTransactionDTO {

    private UUID id;
    private PaymentDTO payment;
    private String gateway;
    private String transactionType;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String gatewayRef;
    private String gatewayResponse;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
