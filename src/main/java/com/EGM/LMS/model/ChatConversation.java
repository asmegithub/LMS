package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Direct (1-on-1) or Group Chat Conversation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_conversations")
public class ChatConversation {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    /**
     * Type of conversation: DIRECT or GROUP
     */
    @Column(nullable = false, length = 32)
    private String type;

    /**
     * Title of the conversation (for group chats or custom channels)
     */
    private String title;

    /**
     * Optional description or topic of the conversation/channel
     */
    @Column(length = 1000)
    private String description;

    /**
     * Optional avatar image URL for the group
     */
    private String avatarUrl;

    /**
     * Optional link to the course if this is a course group or course-related inquiry
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    /**
     * User who created this conversation/group
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    /**
     * Preview of the most recent message
     */
    @Column(length = 1000)
    private String lastMessageText;

    /**
     * Timestamp of the most recent message for fast ordering
     */
    private LocalDateTime lastMessageAt;

    /**
     * Name of the sender of the most recent message
     */
    private String lastMessageSenderName;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
