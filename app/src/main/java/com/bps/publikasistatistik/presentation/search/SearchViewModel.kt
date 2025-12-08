package com.bps.publikasistatistik.presentation.search

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationsUseCase
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
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase
) : ViewModel() {

    // State untuk UI (Loading, Error, Data List)
    private val _state = mutableStateOf(SearchUiState())
    val state: State<SearchUiState> = _state

    // State untuk Input Filter (Query, Category, Year, Sort)
    private val _filterState = mutableStateOf(SearchFilterState())
    val filterState: State<SearchFilterState> = _filterState

    private var searchJob: Job? = null

    init {
        loadSearchHistory() // Load history saat pertama kali dibuka
    }

    // --- LOGIKA UTAMA SEARCH ---

    fun searchPublications() {
        val filters = _filterState.value

        viewModelScope.launch {
            // Cek apakah query kosong DAN tidak ada filter lain yang aktif
            val isQueryEmpty = filters.query.isBlank()
            val isFilterActive = filters.categoryId != null || filters.year != null

            // Jika kosong total -> Kembali ke mode Initial (Tampilkan History)
            if (isQueryEmpty && !isFilterActive) {
                _state.value = _state.value.copy(isInitial = true, publications = emptyList())
                loadSearchHistory() // Refresh history siapa tau ada baru
                return@launch
            }

            // Jika ada query atau filter -> Lakukan Request ke Backend
            // Ubah query string kosong menjadi null agar backend handle dengan benar
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
                            publications = result.data ?: emptyList()
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

        // Debounce: Tunggu 500ms user berhenti ngetik baru request
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (newQuery.isNotBlank()) {
                delay(500L) // Delay 0.5 detik
                searchPublications()
            } else {
                // Jika dihapus sampai kosong, langsung reset ke history
                searchPublications()
            }
        }
    }

    fun onCategoryChange(id: Long?) {
        _filterState.value = _filterState.value.copy(categoryId = id)
        searchPublications() // Langsung refresh
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
        _filterState.value = SearchFilterState() // Reset semua filter ke default
        searchPublications() // Balik ke initial state
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
                if (it is Resource.Success) {
                    _state.value = _state.value.copy(searchHistory = emptyList())
                }
            }
        }
    }
}