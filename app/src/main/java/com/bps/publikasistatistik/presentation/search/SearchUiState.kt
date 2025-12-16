package com.bps.publikasistatistik.presentation.search

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.domain.model.SearchHistory

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val searchResults: List<Publication> = emptyList(),
    val searchHistory: List<SearchHistory> = emptyList(),
    val error: String? = null,
    val isInitial: Boolean = true,

    // --- TAMBAHAN UNTUK SUGGESTION ---
    val suggestions: List<String> = emptyList(), // Daftar saran dari API
    val isSearchingSuggestions: Boolean = false // Loading kecil khusus suggestion
)

// SearchFilterState tetap sama
data class SearchFilterState(
    val query: String = "",
    val categoryId: Long? = null,
    val year: Int? = null,
    val sort: String = "latest"
)