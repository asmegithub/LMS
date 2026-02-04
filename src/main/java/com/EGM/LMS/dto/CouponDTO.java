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
public class CouponDTO {

    private UUID id;
    private String code;
    private String description;
    private String type;
    private BigDecimal value;
    private String currency;
    private BigDecimal minPurchase;
    private BigDecimal maxDiscount;
    private int usageLimit;
    private int usageCount;
    private int perUserLimit;
    private boolean isActive;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
