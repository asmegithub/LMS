package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.BookmarkDTO;
import com.EGM.LMS.dto.CourseDTO;
import com.EGM.LMS.dto.LessonDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.Bookmark;
import com.EGM.LMS.repository.BookmarkRepository;
import com.EGM.LMS.repository.CourseRepository;
import com.EGM.LMS.repository.LessonRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Override
    public BookmarkDTO createBookmark(BookmarkDTO bookmark) {
        var entity = toEntity(bookmark);
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            userRepository.findByEmail(auth.getName()).ifPresent(entity::setUser);
        }
        return toDto(bookmarkRepository.save(entity));
    }

    @Override
    public List<BookmarkDTO> getMyBookmarks(UUID lessonId) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return List.of();
        var user = userRepository.findByEmail(auth.getName());
        if (user.isEmpty()) return List.of();
        var list = lessonId != null
                ? bookmarkRepository.findByUser_IdAndLesson_IdOrderByTimestampAsc(user.get().getId(), lessonId)
                : bookmarkRepository.findByUser_IdOrderByCreatedAtDesc(user.get().getId());
        var dtos = new ArrayList<BookmarkDTO>();
        for (Bookmark b : list) dtos.add(toDto(b));
        return dtos;
    }

    @Override
    public List<BookmarkDTO> getAllBookmarks() {
        var bookmarks = bookmarkRepository.findAll();
        var bookmarkDtos = new java.util.ArrayList<BookmarkDTO>();
        for (Bookmark bookmark : bookmarks) {
            bookmarkDtos.add(toDto(bookmark));
        }
        return bookmarkDtos;
    }

    @Override
    public BookmarkDTO getBookmark(UUID bookmarkId) {
        return toDto(bookmarkRepository.findById(bookmarkId).orElseThrow());
    }

    @Override
    public BookmarkDTO updateBookmark(UUID bookmarkId, BookmarkDTO bookmark) {
        bookmarkRepository.findById(bookmarkId).orElseThrow();
        var entity = toEntity(bookmark);
        entity.setId(bookmarkId);
        return toDto(bookmarkRepository.save(entity));
    }

    @Override
    public void deleteBookmark(UUID bookmarkId) {
        bookmarkRepository.deleteById(bookmarkId);
    }

    private Bookmark toEntity(BookmarkDTO bookmark) {
        var userId = bookmark.getUser() != null ? bookmark.getUser().getId() : null;
        var courseId = bookmark.getCourse() != null ? bookmark.getCourse().getId() : null;
        var lessonId = bookmark.getLesson() != null ? bookmark.getLesson().getId() : null;
        return Bookmark.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .course(courseId != null ? courseRepository.findById(courseId).orElse(null) : null)
                .lesson(lessonId != null ? lessonRepository.findById(lessonId).orElse(null) : null)
                .timestamp(bookmark.getTimestamp())
                .note(bookmark.getNote())
                .build();
    }

    private BookmarkDTO toDto(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .id(bookmark.getId())
                .user(bookmark.getUser() != null ? UserDTO.builder().id(bookmark.getUser().getId()).build() : null)
                .course(bookmark.getCourse() != null ? CourseDTO.builder().id(bookmark.getCourse().getId()).build() : null)
                .lesson(bookmark.getLesson() != null ? LessonDTO.builder().id(bookmark.getLesson().getId()).build() : null)
                .timestamp(bookmark.getTimestamp())
                .note(bookmark.getNote())
                .createdAt(bookmark.getCreatedAt())
                .updatedAt(bookmark.getUpdatedAt())
                .build();
    }
}
