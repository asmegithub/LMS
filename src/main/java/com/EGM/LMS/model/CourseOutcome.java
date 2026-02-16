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
 * Represents a CourseOutcome entity.
 * Defines the specific skills or knowledge a student will acquire.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_outcomes")
@Builder
public class CourseOutcome {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the Course */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Standard outcome text */
    private String text;

    /** * Outcome text in Amharic */
    private String textAm;

    /** * Outcome text in Oromo */
    private String textOm;
    /** * Outcome text in Geez */
    private String textGz;

    /** * Order of appearance in the UI list */
    private int orderIndex;

    /** * Creation timestamp with millisecond precision */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // --- Constructors, Getters, and Setters ---
}