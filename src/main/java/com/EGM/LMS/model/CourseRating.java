package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a CourseRating entity.
 * Stores aggregated rating statistics for a specific course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_ratings")
@Builder
public class CourseRating {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Course */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Count of 1-star ratings */
    private int oneStar;

    /** * Count of 2-star ratings */
    private int twoStar;

    /** * Count of 3-star ratings */
    private int threeStar;

    /** * Count of 4-star ratings */
    private int fourStar;

    /** * Count of 5-star ratings */
    private int fiveStar;

    /** * Total number of ratings received */
    private int totalCount;

    /** * Calculated average (e.g., 4.75) */
    private BigDecimal average;

    /** * Last time the aggregate was recalculated */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // --- Constructors, Getters, and Setters ---
}
