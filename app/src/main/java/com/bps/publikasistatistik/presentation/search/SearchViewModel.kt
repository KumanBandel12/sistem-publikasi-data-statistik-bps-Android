package com.bps.publikasistatistik.presentation.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationsUseCase
import com.bps.publikasistatistik.domain.usecase.publication.GetSearchSuggestionsUseCase
import com.bps.publikasistatistik.domain.usecase.search.ClearSearchHistoryUseCase
import com.bps.publikasistatistik.domain.usecase.search.GetSearchHistoryUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
    private val getSearchSuggestionsUseCase: GetSearchSuggestionsUseCase
) : ViewModel() {

    // State untuk UI (Loading, Error, Data List)
    private val _state = mutableStateOf(SearchUiState())
    val state: State<SearchUiState> = _state

    // State untuk Input Filter (Query, Category, Year, Sort)
    private val _filterState = mutableStateOf(SearchFilterState())
    val filterState: State<SearchFilterState> = _filterState

    private var searchJob: Job? = null
    private var suggestionJob: Job? = null

    init {
        loadSearchHistory() // Load history saat pertama kali dibuka
    }

    // --- LOGIKA UTAMA SEARCH ---

    fun searchPublications() {
        val filters = _filterState.value

        viewModelScope.launch {
            // Saat mulai mencari hasil akhir, kosongkan suggestion agar list-nya hilang
            _state.value = _state.value.copy(suggestions = emptyList())

            val isQueryEmpty = filters.query.isBlank()
            val isFilterActive = filters.categoryId != null || filters.year != null

            if (isQueryEmpty && !isFilterActive) {
                _state.value = _state.value.copy(isInitial = true, searchResults = emptyList())
                loadSearchHistory()
                return@launch
            }

            val queryParam = if (isQueryEmpty) null else filters.query

            getPublicationsUseCase(
                search = queryParam,
                categoryId = filters.categoryId,
                year = filters.year,
                sort = filters.sort
            ).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true, isInitial = false, error = null)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            searchResults = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    // --- EVENT HANDLERS (Dipanggil dari UI) ---

    fun onQueryChange(newQuery: String) {
        _filterState.value = _filterState.value.copy(query = newQuery)

        // 1. Update text di UI langsung
        _state.value = _state.value.copy(query = newQuery)

        // 2. Batalkan job sebelumnya agar tidak spam
        searchJob?.cancel()
        suggestionJob?.cancel()

        suggestionJob = viewModelScope.launch {
            if (newQuery.isNotBlank()) {
                delay(300L) // Debounce dikit
                // Ambil Suggestion (Autocomplete)
                getSearchSuggestionsUseCase(newQuery).collect { result ->
                    if (result is Resource.Success) {
                        _state.value = _state.value.copy(
                            suggestions = result.data ?: emptyList()
                        )
                    }
                }
            } else {
                // Jika dihapus sampai kosong, reset suggestion & tampilkan history
                _state.value = _state.value.copy(suggestions = emptyList(), isInitial = true)
                loadSearchHistory()
            }
        }
    }

    // Saat user klik salah satu saran
    fun onSuggestionClick(suggestion: String) {
        // Set text
        _filterState.value = _filterState.value.copy(query = suggestion)
        _state.value = _state.value.copy(query = suggestion, suggestions = emptyList())

        // Langsung cari hasil akhirnya
        searchPublications()
    }

    fun onCategoryChange(id: Long?) {
        _filterState.value = _filterState.value.copy(categoryId = id)
        searchPublications()
    }

    fun onYearChange(year: Int?) {
        _filterState.value = _filterState.value.copy(year = year)
        searchPublications()
    }

    fun onSortChange(sort: String) {
        _filterState.value = _filterState.value.copy(sort = sort)
        searchPublications()
    }

    fun clearFilters() {
        _filterState.value = SearchFilterState()
        searchPublications()
    }

    // --- HISTORY LOGIC ---

    private fun loadSearchHistory() {
        viewModelScope.launch {
            getSearchHistoryUseCase().collect { result ->
                if (result is Resource.Success) {
                    _state.value = _state.value.copy(searchHistory = result.data ?: emptyList())
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            clearSearchHistoryUseCase().collect {
                if (it is Resource.Success) _state.value = _state.value.copy(searchHistory = emptyList())
            }
        }
    }
}