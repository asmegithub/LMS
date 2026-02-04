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
public class ReferralRewardDTO {

    private UUID id;
    private ReferralDTO referral;
    private UserDTO user;
    private BigDecimal amount;
    private String type;
    private String description;
    private boolean isPaidOut;
    private LocalDateTime paidOutAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
