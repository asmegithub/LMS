package com.EGM.LMS.service;

import com.EGM.LMS.dto.DownloadDTO;

import java.util.List;
import java.util.UUID;

public interface DownloadService {
    DownloadDTO createDownload(DownloadDTO download);
    List<DownloadDTO> getAllDownloads();
    DownloadDTO getDownload(UUID downloadId);
    DownloadDTO updateDownload(UUID downloadId, DownloadDTO download);
    void deleteDownload(UUID downloadId);
}
