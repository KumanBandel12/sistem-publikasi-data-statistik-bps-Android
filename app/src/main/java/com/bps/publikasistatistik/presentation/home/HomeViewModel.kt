package com.bps.publikasistatistik.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.usecase.publication.GetFeaturedPublicationsUseCase
import com.bps.publikasistatistik.domain.usecase.publication.GetMostDownloadedUseCase
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationsUseCase
import com.bps.publikasistatistik.domain.usecase.user.GetProfileUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val getMostDownloadedUseCase: GetMostDownloadedUseCase,
    private val getFeaturedPublicationsUseCase: GetFeaturedPublicationsUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = mutableStateOf(HomeUiState())
    val state: State<HomeUiState> = _state

    // State khusus untuk Refreshing Indicator
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadHomeData()
        loadUserProfile()
        loadFeaturedPublications()
    }

    fun getGreeting(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..10 -> "Selamat Pagi"
            in 11..14 -> "Selamat Siang"
            in 15..18 -> "Selamat Sore"
            else -> "Selamat Malam"
        }
    }

    private fun loadFeaturedPublications() {
        viewModelScope.launch {
            getFeaturedPublicationsUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            featuredPublications = result.data ?: emptyList()
                        )
                    }
                    is Resource.Error -> {
                        // Keep empty list - section will be hidden
                    }
                    is Resource.Loading -> {
                        // Optional: show loading state
                    }
                }
            }
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            getProfileUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = _state.value.copy(user = result.data)
                    }
                    is Resource.Error -> {
                        // Silently fail - user info is not critical for home screen
                    }
                    is Resource.Loading -> {
                        // Do nothing
                    }
                }
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            fetchAll() // Reuse fungsi fetchAll
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchAll()
            loadFeaturedPublications() // Also refresh featured publications
            _isRefreshing.value = false
        }
    }

    private suspend fun fetchAll() {
        // Gunakan async agar request berjalan bersamaan (Paralel)
        val latestDeferred = viewModelScope.async {
            // Reuse usecase yang ada dengan parameter sort
            getPublicationsUseCase(sort = "latest")
        }
        val popularDeferred = viewModelScope.async {
            getMostDownloadedUseCase(limit = 10)
        }

        // Tunggu hasil Latest
        latestDeferred.await().collect { result ->
            if (result is Resource.Success) {
                _state.value = _state.value.copy(latestPublications = result.data ?: emptyList())
            }
        }

        // Tunggu hasil Popular
        popularDeferred.await().collect { result ->
            if (result is Resource.Success) {
                _state.value = _state.value.copy(popularPublications = result.data ?: emptyList())
            }
        }

        // Jika ada error, bisa dihandle di blok else (Resource.Error)
    }
}