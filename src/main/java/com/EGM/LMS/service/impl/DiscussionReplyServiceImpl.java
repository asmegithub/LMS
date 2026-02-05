package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.DiscussionReplyDTO;
import com.EGM.LMS.dto.LessonDiscussionDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.DiscussionReply;
import com.EGM.LMS.repository.DiscussionReplyRepository;
import com.EGM.LMS.repository.LessonDiscussionRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.DiscussionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiscussionReplyServiceImpl implements DiscussionReplyService {
    private final DiscussionReplyRepository discussionReplyRepository;
    private final LessonDiscussionRepository lessonDiscussionRepository;
    private final UserRepository userRepository;

    @Override
    public DiscussionReplyDTO createDiscussionReply(DiscussionReplyDTO discussionReply) {
        return toDto(discussionReplyRepository.save(toEntity(discussionReply)));
    }

    @Override
    public List<DiscussionReplyDTO> getAllDiscussionReplies() {
        var replies = discussionReplyRepository.findAll();
        var replyDtos = new java.util.ArrayList<DiscussionReplyDTO>();
        for (DiscussionReply reply : replies) {
            replyDtos.add(toDto(reply));
        }
        return replyDtos;
    }

    @Override
    public DiscussionReplyDTO getDiscussionReply(UUID discussionReplyId) {
        return toDto(discussionReplyRepository.findById(discussionReplyId).orElseThrow());
    }

    @Override
    public DiscussionReplyDTO updateDiscussionReply(UUID discussionReplyId, DiscussionReplyDTO discussionReply) {
        discussionReplyRepository.findById(discussionReplyId).orElseThrow();
        var entity = toEntity(discussionReply);
        entity.setId(discussionReplyId);
        return toDto(discussionReplyRepository.save(entity));
    }

    @Override
    public void deleteDiscussionReply(UUID discussionReplyId) {
        discussionReplyRepository.deleteById(discussionReplyId);
    }

    private DiscussionReply toEntity(DiscussionReplyDTO discussionReply) {
        var discussionId = discussionReply.getDiscussion() != null ? discussionReply.getDiscussion().getId() : null;
        var userId = discussionReply.getUser() != null ? discussionReply.getUser().getId() : null;
        return DiscussionReply.builder()
                .discussion(discussionId != null ? lessonDiscussionRepository.findById(discussionId).orElse(null) : null)
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .content(discussionReply.getContent())
                .build();
    }

    private DiscussionReplyDTO toDto(DiscussionReply discussionReply) {
        return DiscussionReplyDTO.builder()
                .id(discussionReply.getId())
                .discussion(discussionReply.getDiscussion() != null ? LessonDiscussionDTO.builder().id(discussionReply.getDiscussion().getId()).build() : null)
                .user(discussionReply.getUser() != null ? UserDTO.builder().id(discussionReply.getUser().getId()).build() : null)
                .content(discussionReply.getContent())
                .createdAt(discussionReply.getCreatedAt())
                .updatedAt(discussionReply.getUpdatedAt())
                .build();
    }
}
