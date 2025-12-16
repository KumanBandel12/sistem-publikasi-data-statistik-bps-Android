package com.bps.publikasistatistik.presentation.publication.detail

import com.bps.publikasistatistik.domain.model.Publication

data class PublicationDetailUiState(
    val isLoading: Boolean = false,
    val publication: Publication? = null,
    val error: String? = null,
    val isDeleted: Boolean = false,
    val deleteError: String? = null,
    val isAdmin: Boolean = false
)