package com.EGM.LMS.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import com.EGM.LMS.model.CourseCategory;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses")
@Builder
/**
 * Represents a Course entity mapped from the LMS database schema.
 */
public class Course {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;
    
    /** Foreign Key linking to the Instructor Profile */

    @ManyToOne
    @JoinColumn(name="instructorId",referencedColumnName = "id")
    private User instructor;
    /** Foreign Key linking to the Course Category */
    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CourseCategory category;

    /** Localized titles (English, Amharic, Oromo) */
    private String title;
    private String titleAm;
    private String titleOm;

    /** URL-friendly identifier */
    private String slug;

    /** Localized descriptions */
    private String description;
    private String descriptionAm;
    private String descriptionOm;

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
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- Constructors, Getters, and Setters would go here ---
}


