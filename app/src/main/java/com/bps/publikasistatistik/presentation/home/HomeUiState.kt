package com.bps.publikasistatistik.presentation.home

import com.bps.publikasistatistik.domain.model.Publication

data class HomeUiState(
    val isLoading: Boolean = false,
    val publications: List<Publication> = emptyList(),
    val error: String? = null
)