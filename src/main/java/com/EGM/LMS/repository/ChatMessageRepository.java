package com.EGM.LMS.repository;

import com.EGM.LMS.model.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findAllByConversation_IdOrderByCreatedAtAsc(UUID conversationId);

    Page<ChatMessage> findAllByConversation_IdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m " +
           "WHERE m.conversation.id = :conversationId " +
           "AND m.sender.id <> :userId " +
           "AND (:lastReadAt IS NULL OR m.createdAt > :lastReadAt)")
    long countUnreadForParticipant(@Param("conversationId") UUID conversationId,
                                  @Param("userId") UUID userId,
                                  @Param("lastReadAt") LocalDateTime lastReadAt);

    @Query("SELECT COUNT(m) FROM ChatMessage m " +
           "JOIN ChatParticipant p ON p.conversation.id = m.conversation.id " +
           "WHERE p.user.id = :userId " +
           "AND m.sender.id <> :userId " +
           "AND (p.lastReadAt IS NULL OR m.createdAt > p.lastReadAt)")
    long countTotalUnreadForUser(@Param("userId") UUID userId);
}
