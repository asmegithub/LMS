package com.EGM.LMS.service.impl;

import com.EGM.LMS.dto.SearchHistoryDTO;
import com.EGM.LMS.dto.UserDTO;
import com.EGM.LMS.model.SearchHistory;
import com.EGM.LMS.repository.SearchHistoryRepository;
import com.EGM.LMS.repository.UserRepository;
import com.EGM.LMS.service.SearchHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SearchHistoryServiceImpl implements SearchHistoryService {
    private final SearchHistoryRepository searchHistoryRepository;
    private final UserRepository userRepository;

    @Override
    public SearchHistoryDTO createSearchHistory(SearchHistoryDTO searchHistory) {
        return toDto(searchHistoryRepository.save(toEntity(searchHistory)));
    }

    @Override
    public List<SearchHistoryDTO> getAllSearchHistories() {
        var histories = searchHistoryRepository.findAll();
        var historyDtos = new java.util.ArrayList<SearchHistoryDTO>();
        for (SearchHistory history : histories) {
            historyDtos.add(toDto(history));
        }
        return historyDtos;
    }

    @Override
    public SearchHistoryDTO getSearchHistory(UUID searchHistoryId) {
        return toDto(searchHistoryRepository.findById(searchHistoryId).orElseThrow());
    }

    @Override
    public SearchHistoryDTO updateSearchHistory(UUID searchHistoryId, SearchHistoryDTO searchHistory) {
        searchHistoryRepository.findById(searchHistoryId).orElseThrow();
        var entity = toEntity(searchHistory);
        entity.setId(searchHistoryId);
        return toDto(searchHistoryRepository.save(entity));
    }

    @Override
    public void deleteSearchHistory(UUID searchHistoryId) {
        searchHistoryRepository.deleteById(searchHistoryId);
    }

    private SearchHistory toEntity(SearchHistoryDTO searchHistory) {
        var userId = searchHistory.getUser() != null ? searchHistory.getUser().getId() : null;
        return SearchHistory.builder()
                .user(userId != null ? userRepository.findById(userId).orElse(null) : null)
                .query(searchHistory.getQuery())
                .category(searchHistory.getCategory())
                .resultsCount(searchHistory.getResultsCount())
                .isVisible(searchHistory.isVisible())
                .source(searchHistory.getSource())
                .build();
    }

    private SearchHistoryDTO toDto(SearchHistory searchHistory) {
        return SearchHistoryDTO.builder()
                .id(searchHistory.getId())
                .user(searchHistory.getUser() != null ? UserDTO.builder().id(searchHistory.getUser().getId()).build() : null)
                .query(searchHistory.getQuery())
                .category(searchHistory.getCategory())
                .resultsCount(searchHistory.getResultsCount())
                .isVisible(searchHistory.isVisible())
                .source(searchHistory.getSource())
                .createdAt(searchHistory.getCreatedAt())
                .updatedAt(searchHistory.getUpdatedAt())
                .build();
    }
}
