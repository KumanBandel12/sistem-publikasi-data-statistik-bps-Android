package com.bps.publikasistatistik.presentation.search

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.domain.model.SearchHistory

data class SearchUiState(
    val isLoading: Boolean = false,
    val publications: List<Publication> = emptyList(),
    val searchHistory: List<SearchHistory> = emptyList(),
    val error: String? = null,
    val isInitial: Boolean = true
)

data class SearchFilterState(
    val query: String = "",
    val categoryId: Long? = null,
    val year: Int? = null,
    val sort: String = "latest" // Default sort
)