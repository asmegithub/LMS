package com.EGM.LMS.service;

import com.EGM.LMS.dto.DiscussionReplyDTO;

import java.util.List;
import java.util.UUID;

public interface DiscussionReplyService {
    DiscussionReplyDTO createDiscussionReply(DiscussionReplyDTO discussionReply);
    List<DiscussionReplyDTO> getAllDiscussionReplies();
    DiscussionReplyDTO getDiscussionReply(UUID discussionReplyId);
    DiscussionReplyDTO updateDiscussionReply(UUID discussionReplyId, DiscussionReplyDTO discussionReply);
    void deleteDiscussionReply(UUID discussionReplyId);
}
