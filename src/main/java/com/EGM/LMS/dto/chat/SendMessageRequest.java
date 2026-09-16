package com.EGM.LMS.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendMessageRequest {
    private String content;
    private String messageType; // TEXT, IMAGE, FILE, SYSTEM, VOICE
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private UUID replyToId;
}
