package com.EGM.LMS.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemDTO {
    private UUID id;
    private UUID orderId;
    private CourseDTO course;
    private BigDecimal amount;
    private BigDecimal platformShare;
    private BigDecimal instructorShare;
}
