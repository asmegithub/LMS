package com.EGM.LMS.repository;

import com.EGM.LMS.model.DiscussionReply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface DiscussionReplyRepository extends JpaRepository<DiscussionReply, UUID> {
    List<DiscussionReply> findAllByDiscussion_IdIn(Collection<UUID> discussionIds);
}
