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
public class PaymentDTO {

    private UUID id;
    private String transactionId;
    private UserDTO student;
    private CourseDTO course;
    private BigDecimal amount;
    private String currency;
    private String gateway;
    private String status;
    private BigDecimal netAmount;
    private BigDecimal platformShare;
    private BigDecimal instructorShare;
    private CouponDTO coupon;
    private BigDecimal discountAmount;
    private String referralCode;
    private BigDecimal referralDiscount;
    private String gatewayResponse;
    private String gatewayReference;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
