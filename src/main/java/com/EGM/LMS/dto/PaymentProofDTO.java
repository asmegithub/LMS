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
public class PaymentProofDTO {
    private UUID id;
    private UserDTO student;
    private CourseDTO course;
    private OrderDTO order;
    private PaymentAccountDTO paymentAccount;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String receiptUrl;
    private String originalFileName;
    private String note;
    private UserDTO reviewer;
    private LocalDateTime reviewedAt;
    private String rejectionReason;
    private PaymentDTO payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

