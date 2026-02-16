package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a CourseRequirement entity mapped from the LMS database schema.
 * Defines the prerequisites or expectations for students before taking a course.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_requirements")
@Builder
public class CourseRequirement {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the associated Course
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Standard requirement text
     */
    private String text;

    /** * Requirement text in Amharic
     */
    private String textAm;

    /** * Requirement text in Oromo
     */
    private String textOm;
    /** * Requirement text in Geez
     */
    private String textGz;

    /** * The position of the requirement in a list (used for UI display sorting)
     */
    private int orderIndex;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // --- Standard Constructors, Getters, and Setters ---
}
