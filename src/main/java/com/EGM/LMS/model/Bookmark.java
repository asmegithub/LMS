package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Bookmark entity mapped from the LMS database schema.
 * Allows users to save specific points within a lesson with personal notes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookmarks")
@Builder
public class Bookmark {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the User who created the bookmark
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * Foreign Key linking to the associated Course
     */
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    /** * Foreign Key linking to the specific Lesson being bookmarked
     */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Specific time point in seconds (for video lessons) or order index
     */
    private Integer timestamp;

    /** * Student's personal note associated with this bookmark
     */
    private String note;

    /** * Timestamp of record creation with millisecond precision
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    // --- Standard Constructors, Getters, and Setters ---
}
