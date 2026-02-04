package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a LessonDiscussion entity mapped from the LMS database schema.
 * Facilitates Q&A and community interaction within individual lessons.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "lesson_discussions")
@Builder
public class LessonDiscussion {

    /** * Primary Key */
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the specific Lesson where the comment was made */
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /** * Foreign Key linking to the User (Student or Instructor) who posted */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * The text content of the discussion post */
    private String content;

    /** * Flag to pin important discussions (e.g., instructor announcements) to the top */
    private Boolean isPinned;

    /** * Timestamp of record creation */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
