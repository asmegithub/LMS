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
public class ReferralDTO {

    private UUID id;
    private UserDTO referrer;
    private UserDTO referee;
    private String referralCode;
    private String status;
    private BigDecimal rewardAmount;
    private Boolean rewardGiven;
    private Boolean referredUserPurchased;
    private LocalDateTime rewardedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
