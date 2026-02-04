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
 * Represents an Enrollment entity mapped from the LMS database schema.
 * Manages the relationship between a student and a course, including progress tracking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "enrollments")
@Builder
public class Enrollment {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User (Student) */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Foreign Key linking to the Course */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Foreign Key linking to the associated Payment transaction */
    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    /** * Overall course completion percentage (e.g., 75.50) */
    private BigDecimal progress;

    /** * Number of lessons currently finished by the student */
    private int completedLessonsCount;

    /** * Foreign Key to the last lesson the student opened (for "Resume" feature) */
    private String lastAccessedLessonId;

    /** * Flag indicating if the student has finished the entire course */
    private boolean isCompleted;

    /** * Timestamp of when the student finished the course */
    private LocalDateTime completedAt;

    /** * Timestamp of when the student first enrolled */
    private LocalDateTime enrolledAt;

    /** * Timestamp of the last progress update */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // --- Standard Constructors, Getters, and Setters ---
}
