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
 * Represents a DiscussionReply entity mapped from the LMS database schema.
 * Stores individual responses to top-level lesson discussions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discussion_replies")
@Builder
public class DiscussionReply {

    /** * Primary Key
     */
    @Id
    @GeneratedValue
    @UuidGenerator
        @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /** * Foreign Key linking to the parent LessonDiscussion
     */
    @ManyToOne
    @JoinColumn(name = "discussion_id")
    private LessonDiscussion discussion;

    /** * Foreign Key linking to the User (Student or Instructor) who replied
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    /** * The text content of the reply post
     */
    private String content;

    /** * Timestamp of record creation with millisecond precision
     */
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // --- Standard Constructors, Getters, and Setters ---
}
