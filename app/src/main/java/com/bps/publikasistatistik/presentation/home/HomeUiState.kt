package com.bps.publikasistatistik.presentation.home

import com.bps.publikasistatistik.domain.model.Publication

data class HomeUiState(
    val isLoading: Boolean = false,
    val latestPublications: List<Publication> = emptyList(),
    val popularPublications: List<Publication> = emptyList(),
    val error: String? = null
)