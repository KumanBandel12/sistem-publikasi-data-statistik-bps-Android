package com.bps.publikasistatistik.presentation.home

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.domain.model.User

enum class HomeTab {
    TERBARU,
    POPULER
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val latestPublications: List<Publication> = emptyList(),
    val popularPublications: List<Publication> = emptyList(),
    val selectedTab: HomeTab = HomeTab.TERBARU,
    val user: User? = null,
    val error: String? = null
)