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
 * Represents a LessonNote entity mapped from the LMS database schema.
 * Stores private student notes associated with specific lessons and timestamps.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lesson_notes")
@Builder
public class LessonNote {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific Lesson
     */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Foreign Key linking to the Student who wrote the note
     */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * The private text content of the note
     */
    private String content;

    /** * Specific time point (in seconds) the note refers to (for video lessons)
     */
    private int timestamp;

    /** * Timestamp of record creation with millisecond precision
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}