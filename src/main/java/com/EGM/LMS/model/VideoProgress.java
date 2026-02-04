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
 * Represents a VideoProgress entity mapped from the LMS database schema.
 * Tracks granular student interaction with video lessons.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "video_progresses")
public class VideoProgress {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific Enrollment record */
    @ManyToOne
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    /** * Foreign Key linking to the Lesson being watched */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Foreign Key linking to the Student */
    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    /** * Amount of time (in seconds) the user has actually viewed */
    private Integer watchedDuration;

    /** * The full length of the video in seconds */
    private Integer totalDuration;

    /** * Percentage of the video completed (e.g., 85.50) */
    private BigDecimal watchPercentage;

    /** * The exact second mark where the user stopped watching */
    private Integer lastWatchedPosition;

    /** * The speed at which the user was watching (e.g., 1.5, 2.0) */
    private BigDecimal playbackSpeed;

    /** * Timestamp of record creation */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}