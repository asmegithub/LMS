package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatConversationDTO {
    private UUID id;
    private String type; // DIRECT or GROUP
    private String title;
    private String description;
    private String avatarUrl;
    private UUID courseId;
    private String courseTitle;
    private UUID createdByUserId;
    private String lastMessageText;
    private LocalDateTime lastMessageAt;
    private String lastMessageSenderName;
    private long unreadCount;
    private List<ChatParticipantDTO> participants;
    private ChatParticipantDTO directOtherParticipant;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
