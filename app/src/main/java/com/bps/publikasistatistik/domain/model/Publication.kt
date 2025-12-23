package com.bps.publikasistatistik.domain.model

data class Publication(
    val id: Long,
    val title: String,
    val description: String?,
    val coverUrl: String?, // URL gambar cover
    val fileUrl: String?,  // URL download file PDF
    val year: Int,
    val views: Int,
    val downloads: Int,
    val categoryName: String,
    val subCategoryName: String, // Sub-category name for gradient labels
    val publishDate: String?, // Publish date for featured cards
    val size: String // Ukuran file yang sudah diformat (misal "2.5 MB")
)