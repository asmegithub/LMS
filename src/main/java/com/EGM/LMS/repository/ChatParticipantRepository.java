package com.EGM.LMS.repository;

import com.EGM.LMS.model.ChatParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatParticipantRepository extends JpaRepository<ChatParticipant, UUID> {

    List<ChatParticipant> findAllByConversation_Id(UUID conversationId);

    Optional<ChatParticipant> findByConversation_IdAndUser_Id(UUID conversationId, UUID userId);

    boolean existsByConversation_IdAndUser_Id(UUID conversationId, UUID userId);

    long countByConversation_Id(UUID conversationId);

    void deleteByConversation_IdAndUser_Id(UUID conversationId, UUID userId);
}
