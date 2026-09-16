package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.chat.*;
import com.EGM.LMS.model.*;
import com.EGM.LMS.repository.*;
import com.EGM.LMS.service.ChatService;
import com.EGM.LMS.service.NotificationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChatServiceImpl implements ChatService {

    private final ChatConversationRepository conversationRepository;
    private final ChatParticipantRepository participantRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(readOnly = true)
    public List<ChatConversationDTO> getUserConversations(UUID userId) {
        List<ChatConversation> conversations = conversationRepository.findAllByUserId(userId);
        return conversations.stream()
                .map(c -> toConversationDto(c, userId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ChatConversationDTO getConversation(UUID conversationId, UUID userId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        if (!participantRepository.existsByConversation_IdAndUser_Id(conversationId, userId)) {
            throw new IllegalArgumentException("You are not a member of this conversation.");
        }

        return toConversationDto(conversation, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> getConversationMessages(UUID conversationId, UUID userId) {
        if (!participantRepository.existsByConversation_IdAndUser_Id(conversationId, userId)) {
            throw new IllegalArgumentException("Access denied: Not a participant.");
        }

        List<ChatMessage> messages = messageRepository.findAllByConversation_IdOrderByCreatedAtAsc(conversationId);
        return messages.stream()
                .map(this::toMessageDto)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageDTO sendMessage(UUID conversationId, UUID userId, SendMessageRequest request) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        ChatParticipant senderParticipant = participantRepository.findByConversation_IdAndUser_Id(conversationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("You are not a participant in this conversation."));

        User sender = senderParticipant.getUser();

        ChatMessage replyTo = null;
        if (request.getReplyToId() != null) {
            replyTo = messageRepository.findById(request.getReplyToId()).orElse(null);
        }

        String msgType = request.getMessageType() != null ? request.getMessageType().toUpperCase() : "TEXT";

        ChatMessage message = ChatMessage.builder()
                .conversation(conversation)
                .sender(sender)
                .content(request.getContent() != null ? request.getContent().trim() : "")
                .messageType(msgType)
                .attachmentUrl(request.getAttachmentUrl())
                .attachmentName(request.getAttachmentName())
                .attachmentSize(request.getAttachmentSize())
                .replyTo(replyTo)
                .reactions("{}")
                .isEdited(false)
                .isDeleted(false)
                .build();

        message = messageRepository.save(message);

        // Update conversation summary
        String preview = buildMessagePreview(message);
        conversation.setLastMessageText(preview);
        conversation.setLastMessageAt(LocalDateTime.now());
        conversation.setLastMessageSenderName(getUserDisplayName(sender));
        conversationRepository.save(conversation);

        // Mark sender as having read up to this point
        senderParticipant.setLastReadAt(LocalDateTime.now());
        participantRepository.save(senderParticipant);

        // Notify other participants
        notifyConversationParticipants(conversation, sender, preview);

        return toMessageDto(message);
    }

    @Override
    public ChatConversationDTO startDirectChat(UUID userId, UUID recipientId, UUID courseId) {
        if (userId.equals(recipientId)) {
            throw new IllegalArgumentException("Cannot start a direct conversation with yourself.");
        }

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Recipient not found."));

        // Check if direct conversation already exists between these two
        List<ChatConversation> existing = conversationRepository.findDirectBetweenUsers(userId, recipientId);
        if (!existing.isEmpty()) {
            if (courseId != null) {
                for (ChatConversation c : existing) {
                    if (c.getCourse() != null && courseId.equals(c.getCourse().getId())) {
                        return toConversationDto(c, userId);
                    }
                }
            }
            return toConversationDto(existing.get(0), userId);
        }

        Course course = null;
        if (courseId != null) {
            course = courseRepository.findById(courseId).orElse(null);
        }

        ChatConversation conversation = ChatConversation.builder()
                .type("DIRECT")
                .course(course)
                .createdBy(currentUser)
                .lastMessageText("Conversation started.")
                .lastMessageAt(LocalDateTime.now())
                .lastMessageSenderName(getUserDisplayName(currentUser))
                .build();

        conversation = conversationRepository.save(conversation);

        ChatParticipant p1 = ChatParticipant.builder()
                .conversation(conversation)
                .user(currentUser)
                .role("MEMBER")
                .lastReadAt(LocalDateTime.now())
                .isMuted(false)
                .build();

        ChatParticipant p2 = ChatParticipant.builder()
                .conversation(conversation)
                .user(recipient)
                .role("MEMBER")
                .lastReadAt(null)
                .isMuted(false)
                .build();

        participantRepository.save(p1);
        participantRepository.save(p2);

        // Add welcome system message
        String studentOrParentContext = "";
        if (currentUser.getParentName() != null && !currentUser.getParentName().isBlank()) {
            studentOrParentContext = " (Parent / Guardian: " + currentUser.getParentName() + ")";
        }
        ChatMessage intro = ChatMessage.builder()
                .conversation(conversation)
                .sender(currentUser)
                .content("Direct message thread started by " + getUserDisplayName(currentUser) + studentOrParentContext)
                .messageType("SYSTEM")
                .reactions("{}")
                .isEdited(false)
                .isDeleted(false)
                .build();
        messageRepository.save(intro);

        return toConversationDto(conversation, userId);
    }

    @Override
    public ChatConversationDTO createGroup(UUID userId, CreateConversationRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (request.getTitle() == null || request.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Group title is required.");
        }

        Course course = null;
        if (request.getCourseId() != null) {
            course = courseRepository.findById(request.getCourseId()).orElse(null);
        }

        ChatConversation conversation = ChatConversation.builder()
                .type("GROUP")
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .avatarUrl(request.getAvatarUrl())
                .course(course)
                .createdBy(creator)
                .lastMessageText("Group created.")
                .lastMessageAt(LocalDateTime.now())
                .lastMessageSenderName(getUserDisplayName(creator))
                .build();

        conversation = conversationRepository.save(conversation);

        // Add creator as ADMIN
        ChatParticipant creatorParticipant = ChatParticipant.builder()
                .conversation(conversation)
                .user(creator)
                .role("ADMIN")
                .lastReadAt(LocalDateTime.now())
                .isMuted(false)
                .build();
        participantRepository.save(creatorParticipant);

        // Add initial members
        final ChatConversation savedConversation = conversation;
        if (request.getInitialMemberIds() != null) {
            for (UUID memberId : request.getInitialMemberIds()) {
                if (!memberId.equals(userId)) {
                    userRepository.findById(memberId).ifPresent(member -> {
                        ChatParticipant p = ChatParticipant.builder()
                                .conversation(savedConversation)
                                .user(member)
                                .role("MEMBER")
                                .lastReadAt(null)
                                .isMuted(false)
                                .build();
                        participantRepository.save(p);
                    });
                }
            }
        }

        // Add system message
        ChatMessage systemMsg = ChatMessage.builder()
                .conversation(conversation)
                .sender(creator)
                .content(getUserDisplayName(creator) + " created the group \"" + conversation.getTitle() + "\"")
                .messageType("SYSTEM")
                .reactions("{}")
                .isEdited(false)
                .isDeleted(false)
                .build();
        messageRepository.save(systemMsg);

        return toConversationDto(conversation, userId);
    }

    @Override
    public void addParticipant(UUID conversationId, UUID actorUserId, UUID targetUserId, String role) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        if (!"GROUP".equalsIgnoreCase(conversation.getType())) {
            throw new IllegalArgumentException("Cannot add participants to a direct chat.");
        }

        // Verify actor is a participant (or admin)
        ChatParticipant actor = participantRepository.findByConversation_IdAndUser_Id(conversationId, actorUserId)
                .orElseThrow(() -> new IllegalArgumentException("Access denied."));

        if (!"ADMIN".equalsIgnoreCase(actor.getRole())) {
            throw new IllegalArgumentException("Only group admins can add new participants.");
        }

        if (participantRepository.existsByConversation_IdAndUser_Id(conversationId, targetUserId)) {
            return; // Already in group
        }

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found."));

        ChatParticipant participant = ChatParticipant.builder()
                .conversation(conversation)
                .user(targetUser)
                .role(role != null && "ADMIN".equalsIgnoreCase(role) ? "ADMIN" : "MEMBER")
                .lastReadAt(null)
                .isMuted(false)
                .build();
        participantRepository.save(participant);

        ChatMessage systemMsg = ChatMessage.builder()
                .conversation(conversation)
                .sender(actor.getUser())
                .content(getUserDisplayName(targetUser) + " was added to the group.")
                .messageType("SYSTEM")
                .reactions("{}")
                .isEdited(false)
                .isDeleted(false)
                .build();
        messageRepository.save(systemMsg);
    }

    @Override
    public void removeParticipant(UUID conversationId, UUID actorUserId, UUID participantId) {
        ChatConversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found."));

        ChatParticipant targetParticipant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found."));

        if (!targetParticipant.getConversation().getId().equals(conversationId)) {
            throw new IllegalArgumentException("Participant does not belong to this conversation.");
        }

        boolean isSelf = targetParticipant.getUser().getId().equals(actorUserId);
        if (!isSelf) {
            ChatParticipant actor = participantRepository.findByConversation_IdAndUser_Id(conversationId, actorUserId)
                    .orElseThrow(() -> new IllegalArgumentException("Access denied."));
            if (!"ADMIN".equalsIgnoreCase(actor.getRole())) {
                throw new IllegalArgumentException("Only group admins can remove other participants.");
            }
        }

        participantRepository.delete(targetParticipant);

        ChatMessage systemMsg = ChatMessage.builder()
                .conversation(conversation)
                .sender(targetParticipant.getUser())
                .content(getUserDisplayName(targetParticipant.getUser()) + (isSelf ? " left the group." : " was removed from the group."))
                .messageType("SYSTEM")
                .reactions("{}")
                .isEdited(false)
                .isDeleted(false)
                .build();
        messageRepository.save(systemMsg);
    }

    @Override
    public void markAsRead(UUID conversationId, UUID userId) {
        participantRepository.findByConversation_IdAndUser_Id(conversationId, userId).ifPresent(p -> {
            p.setLastReadAt(LocalDateTime.now());
            participantRepository.save(p);
        });
    }

    @Override
    public ChatMessageDTO toggleReaction(UUID messageId, UUID userId, String emoji) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message not found."));

        if (emoji == null || emoji.isBlank()) {
            throw new IllegalArgumentException("Emoji is required.");
        }

        Map<String, List<String>> reactionsMap = new HashMap<>();
        try {
            if (message.getReactions() != null && !message.getReactions().isBlank()) {
                reactionsMap = objectMapper.readValue(message.getReactions(), new TypeReference<Map<String, List<String>>>() {});
            }
        } catch (Exception e) {
            log.warn("Failed to parse reactions JSON: {}", message.getReactions());
        }

        String userStr = userId.toString();
        List<String> users = reactionsMap.computeIfAbsent(emoji, k -> new ArrayList<>());

        if (users.contains(userStr)) {
            users.remove(userStr);
            if (users.isEmpty()) {
                reactionsMap.remove(emoji);
            }
        } else {
            users.add(userStr);
        }

        try {
            message.setReactions(objectMapper.writeValueAsString(reactionsMap));
        } catch (Exception e) {
            log.error("Failed to serialize reactions", e);
        }

        message = messageRepository.save(message);
        return toMessageDto(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatContactDTO> getAvailableContacts(UUID userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Map<UUID, ChatContactDTO> contactMap = new LinkedHashMap<>();
        String currentRole = currentUser.getRole() != null ? currentUser.getRole().toUpperCase() : "STUDENT";
        if (currentRole.startsWith("ROLE_")) {
            currentRole = currentRole.substring(5);
        }

        // 1. Platform Owners / Admins are available to everyone
        List<User> admins = userRepository.findByRoleIgnoreCase("ADMIN");
        for (User admin : admins) {
            if (!admin.getId().equals(userId)) {
                contactMap.put(admin.getId(), ChatContactDTO.builder()
                        .userId(admin.getId())
                        .name(getUserDisplayName(admin))
                        .email(admin.getEmail())
                        .role("ADMIN")
                        .profileImage(admin.getProfileImage())
                        .contactType("PLATFORM_ADMIN")
                        .build());
            }
        }

        // 2. If student: add instructors of enrolled courses
        if ("STUDENT".equals(currentRole)) {
            List<Enrollment> enrollments = enrollmentRepository.findAllByStudent_Id(userId);
            for (Enrollment en : enrollments) {
                if (en.getCourse() != null && en.getCourse().getInstructor() != null && en.getCourse().getInstructor().getUser() != null) {
                    User instructorUser = en.getCourse().getInstructor().getUser();
                    if (!instructorUser.getId().equals(userId)) {
                        contactMap.put(instructorUser.getId(), ChatContactDTO.builder()
                                .userId(instructorUser.getId())
                                .name(getUserDisplayName(instructorUser))
                                .email(instructorUser.getEmail())
                                .role("INSTRUCTOR")
                                .profileImage(instructorUser.getProfileImage())
                                .courseId(en.getCourse().getId())
                                .courseTitle(en.getCourse().getTitle())
                                .contactType("COURSE_INSTRUCTOR")
                                .build());
                    }
                }
            }
        }

        // 3. If instructor: add students enrolled in their courses
        if ("INSTRUCTOR".equals(currentRole)) {
            List<Enrollment> enrollments = enrollmentRepository.findAllByCourse_Instructor_User_Id(userId);
            for (Enrollment en : enrollments) {
                if (en.getStudent() != null) {
                    User student = en.getStudent();
                    if (!student.getId().equals(userId)) {
                        contactMap.put(student.getId(), ChatContactDTO.builder()
                                .userId(student.getId())
                                .name(getUserDisplayName(student))
                                .email(student.getEmail())
                                .role("STUDENT")
                                .profileImage(student.getProfileImage())
                                .parentName(student.getParentName())
                                .parentEmail(student.getParentEmail())
                                .parentPhone(student.getParentPhone())
                                .parentRelationship(student.getParentRelationship())
                                .courseId(en.getCourse() != null ? en.getCourse().getId() : null)
                                .courseTitle(en.getCourse() != null ? en.getCourse().getTitle() : null)
                                .contactType("ENROLLED_STUDENT")
                                .build());
                    }
                }
            }
        }

        // 4. If admin: can contact any user
        if ("ADMIN".equals(currentRole)) {
            List<User> allInstructors = userRepository.findByRoleIgnoreCase("INSTRUCTOR");
            for (User u : allInstructors) {
                if (!u.getId().equals(userId)) {
                    contactMap.putIfAbsent(u.getId(), ChatContactDTO.builder()
                            .userId(u.getId())
                            .name(getUserDisplayName(u))
                            .email(u.getEmail())
                            .role(u.getRole())
                            .profileImage(u.getProfileImage())
                            .contactType("INSTRUCTOR")
                            .build());
                }
            }

            List<User> allStudents = userRepository.findByRoleIgnoreCase("STUDENT");
            for (User u : allStudents) {
                if (!u.getId().equals(userId)) {
                    contactMap.putIfAbsent(u.getId(), ChatContactDTO.builder()
                            .userId(u.getId())
                            .name(getUserDisplayName(u))
                            .email(u.getEmail())
                            .role(u.getRole())
                            .profileImage(u.getProfileImage())
                            .parentName(u.getParentName())
                            .parentEmail(u.getParentEmail())
                            .parentPhone(u.getParentPhone())
                            .parentRelationship(u.getParentRelationship())
                            .contactType("STUDENT")
                            .build());
                }
            }
        }

        return new ArrayList<>(contactMap.values());
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUnreadCount(UUID userId) {
        return messageRepository.countTotalUnreadForUser(userId);
    }

    // Helper methods
    private void notifyConversationParticipants(ChatConversation conversation, User sender, String preview) {
        List<ChatParticipant> participants = participantRepository.findAllByConversation_Id(conversation.getId());
        for (ChatParticipant p : participants) {
            if (!p.getUser().getId().equals(sender.getId()) && !Boolean.TRUE.equals(p.getIsMuted())) {
                try {
                    String title = "DIRECT".equalsIgnoreCase(conversation.getType())
                            ? "New message from " + getUserDisplayName(sender)
                            : "New message in " + (conversation.getTitle() != null ? conversation.getTitle() : "Group");
                    
                    notificationService.createNotification(com.EGM.LMS.dto.NotificationDTO.builder()
                            .user(com.EGM.LMS.dto.UserDTO.builder().id(p.getUser().getId()).build())
                            .type("CHAT_MESSAGE")
                            .title(title)
                            .message(preview)
                            .relatedType("CHAT_CONVERSATION")
                            .relatedId(conversation.getId().toString())
                            .actionUrl("/dashboard/chat?conversationId=" + conversation.getId())
                            .isRead(false)
                            .build());
                } catch (Exception e) {
                    log.warn("Failed to create in-app notification for chat message: {}", e.getMessage());
                }
            }
        }
    }

    private ChatConversationDTO toConversationDto(ChatConversation c, UUID currentUserId) {
        List<ChatParticipant> participants = participantRepository.findAllByConversation_Id(c.getId());
        List<ChatParticipantDTO> participantDtos = participants.stream()
                .map(this::toParticipantDto)
                .collect(Collectors.toList());

        ChatParticipantDTO directOther = null;
        if ("DIRECT".equalsIgnoreCase(c.getType())) {
            directOther = participantDtos.stream()
                    .filter(p -> !p.getUserId().equals(currentUserId))
                    .findFirst()
                    .orElse(null);
        }

        ChatParticipant currentParticipant = participants.stream()
                .filter(p -> p.getUser().getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        LocalDateTime lastReadAt = currentParticipant != null ? currentParticipant.getLastReadAt() : null;
        long unreadCount = messageRepository.countUnreadForParticipant(c.getId(), currentUserId, lastReadAt);

        String title = c.getTitle();
        if ("DIRECT".equalsIgnoreCase(c.getType()) && directOther != null) {
            title = directOther.getName();
            if (directOther.getParentName() != null && !directOther.getParentName().isBlank()) {
                title += " (Parent: " + directOther.getParentName() + ")";
            }
        }

        return ChatConversationDTO.builder()
                .id(c.getId())
                .type(c.getType())
                .title(title)
                .description(c.getDescription())
                .avatarUrl(c.getAvatarUrl() != null ? c.getAvatarUrl() : (directOther != null ? directOther.getProfileImage() : null))
                .courseId(c.getCourse() != null ? c.getCourse().getId() : null)
                .courseTitle(c.getCourse() != null ? c.getCourse().getTitle() : null)
                .createdByUserId(c.getCreatedBy() != null ? c.getCreatedBy().getId() : null)
                .lastMessageText(c.getLastMessageText())
                .lastMessageAt(c.getLastMessageAt())
                .lastMessageSenderName(c.getLastMessageSenderName())
                .unreadCount(unreadCount)
                .participants(participantDtos)
                .directOtherParticipant(directOther)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private ChatParticipantDTO toParticipantDto(ChatParticipant p) {
        User u = p.getUser();
        return ChatParticipantDTO.builder()
                .id(p.getId())
                .userId(u.getId())
                .name(getUserDisplayName(u))
                .email(u.getEmail())
                .role(p.getRole())
                .userRole(u.getRole())
                .profileImage(u.getProfileImage())
                .parentName(u.getParentName())
                .parentEmail(u.getParentEmail())
                .parentPhone(u.getParentPhone())
                .parentRelationship(u.getParentRelationship())
                .joinedAt(p.getJoinedAt())
                .build();
    }

    private ChatMessageDTO toMessageDto(ChatMessage m) {
        User sender = m.getSender();
        return ChatMessageDTO.builder()
                .id(m.getId())
                .conversationId(m.getConversation().getId())
                .senderId(sender.getId())
                .senderName(getUserDisplayName(sender))
                .senderRole(sender.getRole())
                .senderProfileImage(sender.getProfileImage())
                .parentName(sender.getParentName())
                .parentRelationship(sender.getParentRelationship())
                .content(m.getContent())
                .messageType(m.getMessageType())
                .attachmentUrl(m.getAttachmentUrl())
                .attachmentName(m.getAttachmentName())
                .attachmentSize(m.getAttachmentSize())
                .replyToId(m.getReplyTo() != null ? m.getReplyTo().getId() : null)
                .replyToContent(m.getReplyTo() != null ? m.getReplyTo().getContent() : null)
                .replyToSenderName(m.getReplyTo() != null ? getUserDisplayName(m.getReplyTo().getSender()) : null)
                .reactions(m.getReactions())
                .isEdited(m.getIsEdited())
                .isDeleted(m.getIsDeleted())
                .createdAt(m.getCreatedAt())
                .build();
    }

    private String getUserDisplayName(User u) {
        String name = ((u.getFirstName() != null ? u.getFirstName() : "") + " " +
                       (u.getLastName() != null ? u.getLastName() : "")).trim();
        return name.isEmpty() ? u.getEmail() : name;
    }

    private String buildMessagePreview(ChatMessage message) {
        if ("IMAGE".equalsIgnoreCase(message.getMessageType())) {
            return "📷 Image" + (message.getContent() != null && !message.getContent().isBlank() ? ": " + message.getContent() : "");
        } else if ("FILE".equalsIgnoreCase(message.getMessageType())) {
            return "📎 File: " + (message.getAttachmentName() != null ? message.getAttachmentName() : "attachment");
        } else if ("VOICE".equalsIgnoreCase(message.getMessageType())) {
            return "🎤 Voice message";
        }
        return message.getContent();
    }
}
