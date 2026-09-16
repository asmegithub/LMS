package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String senderRole;
    private String senderProfileImage;
    private String parentName;
    private String parentRelationship;
    private String content;
    private String messageType; // TEXT, IMAGE, FILE, SYSTEM, VOICE
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private UUID replyToId;
    private String replyToContent;
    private String replyToSenderName;
    private String reactions;
    private Boolean isEdited;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
}
