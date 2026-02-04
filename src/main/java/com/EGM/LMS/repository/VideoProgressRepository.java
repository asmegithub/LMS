package com.EGM.LMS.repository;

import com.EGM.LMS.model.VideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VideoProgressRepository extends JpaRepository<VideoProgress, UUID> {
}
