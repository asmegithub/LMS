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
 * Represents a member of a ChatConversation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "chat_participants",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_chat_conversation_user", columnNames = {"conversation_id", "user_id"})
    }
)
public class ChatParticipant {

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
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Role in this conversation: ADMIN or MEMBER
     */
    @Column(nullable = false, length = 32)
    private String role;

    @CreationTimestamp
    private LocalDateTime joinedAt;

    /**
     * Timestamp when the user last read this conversation (for unread count)
     */
    private LocalDateTime lastReadAt;

    private Boolean isMuted;
}
