package com.EGM.LMS.controller;

import com.EGM.LMS.dto.DownloadDTO;
import com.EGM.LMS.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/downloads")
public class DownloadController {
    private final DownloadService downloadService;

    @PostMapping
    ResponseEntity<DownloadDTO> createDownload(@RequestBody DownloadDTO downloadDto) {
        return ResponseEntity.ok(downloadService.createDownload(downloadDto));
    }

    @GetMapping
    ResponseEntity<List<DownloadDTO>> getAllDownloads() {
        return ResponseEntity.ok(downloadService.getAllDownloads());
    }

    @GetMapping("/{downloadId}")
    ResponseEntity<DownloadDTO> getDownload(@PathVariable UUID downloadId) {
        return ResponseEntity.ok(downloadService.getDownload(downloadId));
    }

    @PutMapping("/{downloadId}")
    ResponseEntity<DownloadDTO> updateDownload(@PathVariable UUID downloadId, @RequestBody DownloadDTO downloadDto) {
        return ResponseEntity.ok(downloadService.updateDownload(downloadId, downloadDto));
    }

    @DeleteMapping("/{downloadId}")
    ResponseEntity<Void> deleteDownload(@PathVariable UUID downloadId) {
        downloadService.deleteDownload(downloadId);
        return ResponseEntity.noContent().build();
    }
}
