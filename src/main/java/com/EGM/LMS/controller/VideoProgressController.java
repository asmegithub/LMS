package com.EGM.LMS.controller;

import com.EGM.LMS.dto.VideoProgressDTO;
import com.EGM.LMS.service.VideoProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/video-progresses")
public class VideoProgressController {
    private final VideoProgressService videoProgressService;

    @PostMapping
    ResponseEntity<VideoProgressDTO> createVideoProgress(@RequestBody VideoProgressDTO videoProgressDto) {
        return ResponseEntity.ok(videoProgressService.createVideoProgress(videoProgressDto));
    }

    @GetMapping("/by-enrollment-lesson")
    ResponseEntity<VideoProgressDTO> getByEnrollmentAndLesson(
            @RequestParam UUID enrollmentId,
            @RequestParam UUID lessonId) {
        return videoProgressService.getByEnrollmentAndLesson(enrollmentId, lessonId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(videoProgressService.emptyProgressDto()));
    }

    @PostMapping("/upsert")
    ResponseEntity<VideoProgressDTO> upsertProgress(@RequestBody java.util.Map<String, Object> body) {
        UUID enrollmentId = UUID.fromString((String) body.get("enrollmentId"));
        UUID lessonId = UUID.fromString((String) body.get("lessonId"));
        Integer lastWatchedPosition = body.get("lastWatchedPosition") != null ? ((Number) body.get("lastWatchedPosition")).intValue() : null;
        Integer watchedDuration = body.get("watchedDuration") != null ? ((Number) body.get("watchedDuration")).intValue() : null;
        Integer totalDuration = body.get("totalDuration") != null ? ((Number) body.get("totalDuration")).intValue() : null;
        return ResponseEntity.ok(videoProgressService.upsertProgress(enrollmentId, lessonId, lastWatchedPosition, watchedDuration, totalDuration));
    }

    @GetMapping
    ResponseEntity<List<VideoProgressDTO>> getAllVideoProgresses() {
        return ResponseEntity.ok(videoProgressService.getAllVideoProgresses());
    }

    @GetMapping("/{videoProgressId}")
    ResponseEntity<VideoProgressDTO> getVideoProgress(@PathVariable UUID videoProgressId) {
        return ResponseEntity.ok(videoProgressService.getVideoProgress(videoProgressId));
    }

    @PutMapping("/{videoProgressId}")
    ResponseEntity<VideoProgressDTO> updateVideoProgress(@PathVariable UUID videoProgressId, @RequestBody VideoProgressDTO videoProgressDto) {
        return ResponseEntity.ok(videoProgressService.updateVideoProgress(videoProgressId, videoProgressDto));
    }

    @DeleteMapping("/{videoProgressId}")
    ResponseEntity<Void> deleteVideoProgress(@PathVariable UUID videoProgressId) {
        videoProgressService.deleteVideoProgress(videoProgressId);
        return ResponseEntity.noContent().build();
    }
}
