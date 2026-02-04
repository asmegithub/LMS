package com.EGM.LMS.repository;

import com.EGM.LMS.model.Download;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DownloadRepository extends JpaRepository<Download, UUID> {
}
