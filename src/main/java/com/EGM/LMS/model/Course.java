package com.EGM.LMS.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
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
    @JoinColumn(name = "instructor_id")
    private InstructorProfile instructor;
    /** Foreign Key linking to the Course Category */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private CourseCategory category;

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
    private int totalDuration;
    private int totalLessons;
    @Builder.Default
    private int enrollmentCount=0;

    @Builder.Default
    private BigDecimal averageRating=new BigDecimal(0);

    @Builder.Default
    private int totalReviews=0;

    /** Flags */
    @Builder.Default
    private boolean isFeatured=false;
    @Builder.Default
    private boolean isPopular=false;
    @Builder.Default
    private boolean isPublished=false;

    /** Management Timestamps */
    private LocalDateTime publishedAt;
    private LocalDateTime rejectedAt;
    
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Constructors, Getters, and Setters would go here ---
}


