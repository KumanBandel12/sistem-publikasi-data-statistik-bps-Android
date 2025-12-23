package com.bps.publikasistatistik.presentation.upload

import android.net.Uri

data class UploadPublicationUiState(
    val title: String = "",
    val description: String = "",
    val author: String = "",
    val categoryId: Long? = null,
    val subCategoryId: Long? = null,
    val year: Int? = null,
    val publishDate: String = "",
    val catalogNumber: String = "",
    val publicationNumber: String = "",
    val issn: String = "",
    val frequency: String = "",
    val language: String = "",
    val fileUri: Uri? = null,
    val isFeatured: Boolean = false,
    val errors: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false
)
