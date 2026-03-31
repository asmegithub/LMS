package com.EGM.LMS.repository;

import com.EGM.LMS.model.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface BookmarkRepository extends JpaRepository<Bookmark, UUID> {
    List<Bookmark> findByUser_IdOrderByCreatedAtDesc(UUID userId);
    List<Bookmark> findByUser_IdAndLesson_IdOrderByTimestampAsc(UUID userId, UUID lessonId);
    List<Bookmark> findAllByLesson_IdIn(Collection<UUID> lessonIds);
}
