package com.EGM.LMS.service;

import com.EGM.LMS.dto.SearchHistoryDTO;

import java.util.List;
import java.util.UUID;

public interface SearchHistoryService {
    SearchHistoryDTO createSearchHistory(SearchHistoryDTO searchHistory);
    List<SearchHistoryDTO> getAllSearchHistories();
    SearchHistoryDTO getSearchHistory(UUID searchHistoryId);
    SearchHistoryDTO updateSearchHistory(UUID searchHistoryId, SearchHistoryDTO searchHistory);
    void deleteSearchHistory(UUID searchHistoryId);
}
