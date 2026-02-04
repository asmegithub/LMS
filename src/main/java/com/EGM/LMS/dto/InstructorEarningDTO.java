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
public class InstructorEarningDTO {

    private UUID id;
    private InstructorProfileDTO instructorProfile;
    private BigDecimal totalEarnings;
    private BigDecimal totalWithdrawn;
    private BigDecimal currentBalance;
    private BigDecimal lastMonthEarning;
    private LocalDateTime lastWithdrawnAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
