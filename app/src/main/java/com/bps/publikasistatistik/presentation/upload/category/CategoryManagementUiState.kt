package com.bps.publikasistatistik.presentation.upload.category

import com.bps.publikasistatistik.domain.model.Category

data class CategoryManagementUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Long? = null,
    val subCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showAddEditDialog: Boolean = false,
    val editingSubCategory: Category? = null,
    val showDeleteDialog: Boolean = false,
    val deletingSubCategoryId: Long? = null
)
