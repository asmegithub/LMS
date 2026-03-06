package com.EGM.LMS.repository;

import com.EGM.LMS.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    java.util.List<Bookmark> findByUser_IdOrderByCreatedAtDesc(UUID userId);
    java.util.List<Bookmark> findByUser_IdAndLesson_IdOrderByTimestampAsc(UUID userId, UUID lessonId);
}
