package com.EGM.LMS.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an individual message in a ChatConversation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ChatConversation conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    /**
     * The text content or markdown of the message
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Type: TEXT, IMAGE, FILE, SYSTEM, VOICE
     */
    @Column(length = 32, nullable = false)
    private String messageType;

    /**
     * URL of attached media/file if messageType is IMAGE, FILE, or VOICE
     */
    @Column(length = 1000)
    private String attachmentUrl;

    /**
     * Original filename of the attachment
     */
    private String attachmentName;

    /**
     * Size in bytes of the attachment
     */
    private Long attachmentSize;

    /**
     * Optional message being replied to
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reply_to_id")
    private ChatMessage replyTo;

    /**
     * Serialized reactions e.g. {"👍":["userId1"],"❤️":["userId2"]}
     */
    @Column(columnDefinition = "TEXT")
    private String reactions;

    private Boolean isEdited;

    private Boolean isDeleted;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
