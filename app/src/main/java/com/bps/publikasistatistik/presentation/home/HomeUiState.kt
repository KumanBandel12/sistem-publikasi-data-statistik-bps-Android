package com.bps.publikasistatistik.presentation.home

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.domain.model.User

data class HomeUiState(
    val isLoading: Boolean = false,
    val latestPublications: List<Publication> = emptyList(),
    val popularPublications: List<Publication> = emptyList(),
    val featuredPublications: List<Publication> = emptyList(),
    val user: User? = null,
    val error: String? = null
)