package com.EGM.LMS.controller;

import com.EGM.LMS.dto.chat.*;
import com.EGM.LMS.model.User;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    @GetMapping("/conversations")
    public ResponseEntity<List<ChatConversationDTO>> getConversations(Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.getUserConversations(user.getId()));
    }

    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<ChatConversationDTO> getConversation(@PathVariable UUID conversationId,
                                                               Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.getConversation(conversationId, user.getId()));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getMessages(@PathVariable UUID conversationId,
                                                            Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.getConversationMessages(conversationId, user.getId()));
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ChatMessageDTO> sendMessage(@PathVariable UUID conversationId,
                                                      @RequestBody SendMessageRequest request,
                                                      Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.sendMessage(conversationId, user.getId(), request));
    }

    @PostMapping("/direct")
    public ResponseEntity<ChatConversationDTO> startDirectChat(@RequestBody Map<String, String> body,
                                                               Authentication authentication) {
        User user = resolveUser(authentication);
        UUID recipientId = UUID.fromString(body.get("recipientId"));
        UUID courseId = body.containsKey("courseId") && body.get("courseId") != null && !body.get("courseId").isBlank()
                ? UUID.fromString(body.get("courseId"))
                : null;

        return ResponseEntity.ok(chatService.startDirectChat(user.getId(), recipientId, courseId));
    }

    @PostMapping("/group")
    public ResponseEntity<ChatConversationDTO> createGroup(@RequestBody CreateConversationRequest request,
                                                           Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.createGroup(user.getId(), request));
    }

    @PostMapping("/conversations/{conversationId}/participants")
    public ResponseEntity<Void> addParticipant(@PathVariable UUID conversationId,
                                               @RequestBody Map<String, String> body,
                                               Authentication authentication) {
        User user = resolveUser(authentication);
        UUID targetUserId = UUID.fromString(body.get("userId"));
        String role = body.getOrDefault("role", "MEMBER");
        chatService.addParticipant(conversationId, user.getId(), targetUserId, role);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/conversations/{conversationId}/participants/{participantId}")
    public ResponseEntity<Void> removeParticipant(@PathVariable UUID conversationId,
                                                  @PathVariable UUID participantId,
                                                  Authentication authentication) {
        User user = resolveUser(authentication);
        chatService.removeParticipant(conversationId, user.getId(), participantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/conversations/{conversationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable UUID conversationId,
                                          Authentication authentication) {
        User user = resolveUser(authentication);
        chatService.markAsRead(conversationId, user.getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/messages/{messageId}/reaction")
    public ResponseEntity<ChatMessageDTO> toggleReaction(@PathVariable UUID messageId,
                                                         @RequestBody Map<String, String> body,
                                                         Authentication authentication) {
        User user = resolveUser(authentication);
        String emoji = body.get("emoji");
        return ResponseEntity.ok(chatService.toggleReaction(messageId, user.getId(), emoji));
    }

    @GetMapping("/contacts")
    public ResponseEntity<List<ChatContactDTO>> getContacts(Authentication authentication) {
        User user = resolveUser(authentication);
        return ResponseEntity.ok(chatService.getAvailableContacts(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication authentication) {
        User user = resolveUser(authentication);
        long count = chatService.getTotalUnreadCount(user.getId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    private User resolveUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Unauthorized");
        }
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
