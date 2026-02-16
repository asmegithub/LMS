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
 * Represents a LessonProgress entity mapped from the LMS database schema.
 * Tracks the completion status of individual lessons for each enrolled student.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lesson_progresses")
@Builder
public class LessonProgress {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the overarching Enrollment record
     */
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    /** * Foreign Key linking to the specific Lesson
     */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Foreign Key linking to the Student
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Current status of the lesson (e.g., NOT_STARTED, IN_PROGRESS, COMPLETED)
     */
    private String status; // Recommended: Use an Enum

    /** * Timestamp of when the student finished the lesson
     */
    private LocalDateTime completedAt;

    /** * Timestamp of record creation
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}