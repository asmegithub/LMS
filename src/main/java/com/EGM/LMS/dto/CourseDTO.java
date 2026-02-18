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
public class CourseDTO {

    private UUID id;
    /** Foreign Key linking to the Instructor Profile */

    private UserDTO instructor;
    private UUID instructorId;

    private CourseCategoryDTO category;
    private UUID categoryId;


    /** Localized titles (English, Amharic, Oromo) */
    private String title;
    private String titleAm;
    private String titleOm;
    private String titleGz;


    /** URL-friendly identifier */
    private String slug;

    /** Localized descriptions */
    private String description;
    private String descriptionAm;
    private String descriptionOm;
    private String descriptionGz;

    /** Media resources URLs */
    private String thumbnail;
    private String previewVideo;

    /** Pricing details */
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String currency;

    /** Course classification */
    private String level; // Map to Enum if preferred (e.g., BEGINNER, INTERMEDIATE)
    private String status; // Map to Enum if preferred (e.g., PUBLISHED, DRAFT)

    /** Metrics and Stats */
    private Integer totalDuration;
    private Integer totalLessons;
    private Integer enrollmentCount;
    private BigDecimal averageRating;
    private Integer totalReviews;

    /** Flags */
    private Boolean isFeatured;
    private Boolean isPopular;

    /** Management Timestamps */
    private LocalDateTime publishedAt;
    private LocalDateTime rejectedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
