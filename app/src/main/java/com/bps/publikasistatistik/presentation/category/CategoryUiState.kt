package com.bps.publikasistatistik.presentation.category

import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.domain.model.Publication

data class CategoryUiState(
    // Global Loading & Error
    val isLoading: Boolean = false,
    val error: String? = null,

    // Data Utama (Grid Induk)
    val parentCategories: List<Category> = emptyList(),

    // State untuk Mode Detail (Setelah klik kategori induk)
    val isDetailMode: Boolean = false, // True jika sedang melihat detail kategori
    val selectedParentCategory: Category? = null,
    val subCategories: List<Category> = emptyList(),
    val selectedSubCategory: Category? = null, // Sub kategori yang dipilih di dropdown

    // Data Publikasi (Hasil Filter)
    val publications: List<Publication> = emptyList(),
    val isPublicationsLoading: Boolean = false,

    val categoryTree: List<Category> = emptyList(),
    val isTreeView: Boolean = false
)