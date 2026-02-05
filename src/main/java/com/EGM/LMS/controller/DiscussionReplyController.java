package com.EGM.LMS.controller;

import com.EGM.LMS.dto.DiscussionReplyDTO;
import com.EGM.LMS.service.DiscussionReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/discussion-replies")
public class DiscussionReplyController {
    private final DiscussionReplyService discussionReplyService;

    @PostMapping
    ResponseEntity<DiscussionReplyDTO> createDiscussionReply(@RequestBody DiscussionReplyDTO discussionReplyDto) {
        return ResponseEntity.ok(discussionReplyService.createDiscussionReply(discussionReplyDto));
    }

    @GetMapping
    ResponseEntity<List<DiscussionReplyDTO>> getAllDiscussionReplies() {
        return ResponseEntity.ok(discussionReplyService.getAllDiscussionReplies());
    }

    @GetMapping("/{discussionReplyId}")
    ResponseEntity<DiscussionReplyDTO> getDiscussionReply(@PathVariable UUID discussionReplyId) {
        return ResponseEntity.ok(discussionReplyService.getDiscussionReply(discussionReplyId));
    }

    @PutMapping("/{discussionReplyId}")
    ResponseEntity<DiscussionReplyDTO> updateDiscussionReply(@PathVariable UUID discussionReplyId, @RequestBody DiscussionReplyDTO discussionReplyDto) {
        return ResponseEntity.ok(discussionReplyService.updateDiscussionReply(discussionReplyId, discussionReplyDto));
    }

    @DeleteMapping("/{discussionReplyId}")
    ResponseEntity<Void> deleteDiscussionReply(@PathVariable UUID discussionReplyId) {
        discussionReplyService.deleteDiscussionReply(discussionReplyId);
        return ResponseEntity.noContent().build();
    }
}
