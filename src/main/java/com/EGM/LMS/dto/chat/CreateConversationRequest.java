package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateConversationRequest {
    private String type; // DIRECT or GROUP
    private String title;
    private String description;
    private String avatarUrl;
    private UUID courseId;
    private UUID recipientUserId; // For DIRECT chat
    private List<UUID> initialMemberIds; // For GROUP chat
}
