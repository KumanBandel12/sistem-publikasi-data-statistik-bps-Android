package com.bps.publikasistatistik.presentation.home

import com.bps.publikasistatistik.domain.model.Publication

enum class HomeTab {
    TERBARU,
    POPULER
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val latestPublications: List<Publication> = emptyList(),
    val popularPublications: List<Publication> = emptyList(),
    val selectedTab: HomeTab = HomeTab.TERBARU,
    val error: String? = null
)