package com.EGM.LMS.controller;

import com.EGM.LMS.dto.SearchHistoryDTO;
import com.EGM.LMS.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search-histories")
public class SearchHistoryController {
    private final SearchHistoryService searchHistoryService;

    @PostMapping
    ResponseEntity<SearchHistoryDTO> createSearchHistory(@RequestBody SearchHistoryDTO searchHistoryDto) {
        return ResponseEntity.ok(searchHistoryService.createSearchHistory(searchHistoryDto));
    }

    @GetMapping
    ResponseEntity<List<SearchHistoryDTO>> getAllSearchHistories() {
        return ResponseEntity.ok(searchHistoryService.getAllSearchHistories());
    }

    @GetMapping("/{searchHistoryId}")
    ResponseEntity<SearchHistoryDTO> getSearchHistory(@PathVariable UUID searchHistoryId) {
        return ResponseEntity.ok(searchHistoryService.getSearchHistory(searchHistoryId));
    }

    @PutMapping("/{searchHistoryId}")
    ResponseEntity<SearchHistoryDTO> updateSearchHistory(@PathVariable UUID searchHistoryId, @RequestBody SearchHistoryDTO searchHistoryDto) {
        return ResponseEntity.ok(searchHistoryService.updateSearchHistory(searchHistoryId, searchHistoryDto));
    }

    @DeleteMapping("/{searchHistoryId}")
    ResponseEntity<Void> deleteSearchHistory(@PathVariable UUID searchHistoryId) {
        searchHistoryService.deleteSearchHistory(searchHistoryId);
        return ResponseEntity.noContent().build();
    }
}
