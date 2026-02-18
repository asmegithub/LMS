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
public class InstructorProfileDTO {

    private UUID id;
    private UserDTO user;
    private String headline;
    private String headlineAm;
    private String headlineOm;
    private String headlineGz;
    private String biography;
    private String biographyAm;
    private String biographyOm;
    private String biographyGz;
    private String expertise;
    private String socialLinks;
    private Integer totalStudents;
    private Integer totalCourses;
    private BigDecimal totalRevenue;
    private BigDecimal averageRating;
    private Boolean isVerified;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
