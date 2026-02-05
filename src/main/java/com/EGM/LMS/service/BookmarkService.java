package com.EGM.LMS.service;

import com.EGM.LMS.dto.BookmarkDTO;

import java.util.List;
import java.util.UUID;

public interface BookmarkService {
    BookmarkDTO createBookmark(BookmarkDTO bookmark);
    List<BookmarkDTO> getAllBookmarks();
    BookmarkDTO getBookmark(UUID bookmarkId);
    BookmarkDTO updateBookmark(UUID bookmarkId, BookmarkDTO bookmark);
    void deleteBookmark(UUID bookmarkId);
}
