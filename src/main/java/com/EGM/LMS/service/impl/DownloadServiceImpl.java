package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.DownloadDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Download;
import com.EGM.LMS.repository.DownloadRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.DownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DownloadServiceImpl implements DownloadService {
    private final DownloadRepository downloadRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    @Override
    public DownloadDTO createDownload(DownloadDTO download) {
        return toDto(downloadRepository.save(toEntity(download)));
    }

    @Override
    public List<DownloadDTO> getAllDownloads() {
        var downloads = downloadRepository.findAll();
        var downloadDtos = new java.util.ArrayList<DownloadDTO>();
        for (Download download : downloads) {
            downloadDtos.add(toDto(download));
        }
        return downloadDtos;
    }

    @Override
    public DownloadDTO getDownload(UUID downloadId) {
        return toDto(downloadRepository.findById(downloadId).orElseThrow());
    }

    @Override
    public DownloadDTO updateDownload(UUID downloadId, DownloadDTO download) {
        downloadRepository.findById(downloadId).orElseThrow();
        var entity = toEntity(download);
        entity.setId(downloadId);
        return toDto(downloadRepository.save(entity));
    }

    @Override
    public void deleteDownload(UUID downloadId) {
        downloadRepository.deleteById(downloadId);
    }

    private Download toEntity(DownloadDTO download) {
        var userId = download.getUser() != null ? download.getUser().getId() : null;
        var lessonId = download.getLesson() != null ? download.getLesson().getId() : null;
        return Download.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .videoQuality(download.getVideoQuality())
                .fileUrl(download.getFileUrl())
                .fileSize(download.getFileSize())
                .expiresAt(download.getExpiresAt())
                .downloadedAt(download.getDownloadedAt())
                .build();
    }

    private DownloadDTO toDto(Download download) {
        return DownloadDTO.builder()
                .id(download.getId())
                .user(download.getUser() != null ? UserDTO.builder().id(download.getUser().getId()).build() : null)
                .lesson(download.getLesson() != null ? LessonDTO.builder().id(download.getLesson().getId()).build() : null)
                .videoQuality(download.getVideoQuality())
                .fileUrl(download.getFileUrl())
                .fileSize(download.getFileSize())
                .expiresAt(download.getExpiresAt())
                .downloadedAt(download.getDownloadedAt())
                .createdAt(download.getCreatedAt())
                .updatedAt(download.getUpdatedAt())
                .build();
    }
}
