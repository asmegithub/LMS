package com.EGM.LMS.repository;

import com.EGM.LMS.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, UUID> {

    @Query("SELECT c FROM ChatConversation c " +
           "JOIN ChatParticipant p ON p.conversation.id = c.id " +
           "WHERE p.user.id = :userId " +
           "ORDER BY COALESCE(c.lastMessageAt, c.createdAt) DESC")
    List<ChatConversation> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT c FROM ChatConversation c " +
           "JOIN ChatParticipant p1 ON p1.conversation.id = c.id " +
           "JOIN ChatParticipant p2 ON p2.conversation.id = c.id " +
           "WHERE c.type = 'DIRECT' " +
           "AND p1.user.id = :user1Id " +
           "AND p2.user.id = :user2Id")
    List<ChatConversation> findDirectBetweenUsers(@Param("user1Id") UUID user1Id, @Param("user2Id") UUID user2Id);

    @Query("SELECT c FROM ChatConversation c " +
           "JOIN ChatParticipant p1 ON p1.conversation.id = c.id " +
           "JOIN ChatParticipant p2 ON p2.conversation.id = c.id " +
           "WHERE c.type = 'DIRECT' " +
           "AND p1.user.id = :user1Id " +
           "AND p2.user.id = :user2Id " +
           "AND c.course.id = :courseId")
    Optional<ChatConversation> findDirectBetweenUsersForCourse(@Param("user1Id") UUID user1Id,
                                                              @Param("user2Id") UUID user2Id,
                                                              @Param("courseId") UUID courseId);

    List<ChatConversation> findAllByCourse_IdAndType(UUID courseId, String type);
}
