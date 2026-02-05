package com.EGM.LMS.controller;

import com.EGM.LMS.dto.BookmarkDTO;
import com.EGM.LMS.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping
    ResponseEntity<BookmarkDTO> createBookmark(@RequestBody BookmarkDTO bookmarkDto) {
        return ResponseEntity.ok(bookmarkService.createBookmark(bookmarkDto));
    }

    @GetMapping
    ResponseEntity<List<BookmarkDTO>> getAllBookmarks() {
        return ResponseEntity.ok(bookmarkService.getAllBookmarks());
    }

    @GetMapping("/{bookmarkId}")
    ResponseEntity<BookmarkDTO> getBookmark(@PathVariable UUID bookmarkId) {
        return ResponseEntity.ok(bookmarkService.getBookmark(bookmarkId));
    }

    @PutMapping("/{bookmarkId}")
    ResponseEntity<BookmarkDTO> updateBookmark(@PathVariable UUID bookmarkId, @RequestBody BookmarkDTO bookmarkDto) {
        return ResponseEntity.ok(bookmarkService.updateBookmark(bookmarkId, bookmarkDto));
    }

    @DeleteMapping("/{bookmarkId}")
    ResponseEntity<Void> deleteBookmark(@PathVariable UUID bookmarkId) {
        bookmarkService.deleteBookmark(bookmarkId);
        return ResponseEntity.noContent().build();
    }
}
