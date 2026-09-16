package com.EGM.LMS.service;

import com.EGM.LMS.dto.chat.*;

import java.util.List;
import java.util.UUID;

public interface ChatService {

    List<ChatConversationDTO> getUserConversations(UUID userId);

    ChatConversationDTO getConversation(UUID conversationId, UUID userId);

    List<ChatMessageDTO> getConversationMessages(UUID conversationId, UUID userId);

    ChatMessageDTO sendMessage(UUID conversationId, UUID userId, SendMessageRequest request);

    ChatConversationDTO startDirectChat(UUID userId, UUID recipientId, UUID courseId);

    ChatConversationDTO createGroup(UUID userId, CreateConversationRequest request);

    void addParticipant(UUID conversationId, UUID actorUserId, UUID targetUserId, String role);

    void removeParticipant(UUID conversationId, UUID actorUserId, UUID participantId);

    void markAsRead(UUID conversationId, UUID userId);

    ChatMessageDTO toggleReaction(UUID messageId, UUID userId, String emoji);

    List<ChatContactDTO> getAvailableContacts(UUID userId);

    long getTotalUnreadCount(UUID userId);
}
