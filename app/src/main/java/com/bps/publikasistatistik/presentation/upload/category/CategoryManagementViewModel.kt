package com.bps.publikasistatistik.presentation.upload.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.domain.repository.CategoryRepository
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryManagementViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryManagementUiState())
    val uiState = _uiState.asStateFlow()

    private val _operationState = MutableStateFlow<Resource<String>?>(null)
    val operationState = _operationState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.getCategories().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                categories = resource.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                        // Load subcategories for first category by default
                        resource.data?.firstOrNull()?.let { firstCategory ->
                            selectCategory(firstCategory.id)
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun selectCategory(categoryId: Long) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        loadSubCategories(categoryId)
    }

    private fun loadSubCategories(categoryId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoryRepository.getSubCategories(categoryId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _uiState.update {
                            it.copy(
                                subCategories = resource.data ?: emptyList(),
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                subCategories = emptyList(),
                                isLoading = false,
                                error = resource.message
                            )
                        }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }

    fun showAddDialog() {
        _uiState.update {
            it.copy(
                showAddEditDialog = true,
                editingSubCategory = null
            )
        }
    }

    fun showEditDialog(subCategory: Category) {
        _uiState.update {
            it.copy(
                showAddEditDialog = true,
                editingSubCategory = subCategory
            )
        }
    }

    fun hideAddEditDialog() {
        _uiState.update {
            it.copy(
                showAddEditDialog = false,
                editingSubCategory = null
            )
        }
    }

    fun showDeleteDialog(subCategoryId: Long) {
        _uiState.update {
            it.copy(
                showDeleteDialog = true,
                deletingSubCategoryId = subCategoryId
            )
        }
    }

    fun hideDeleteDialog() {
        _uiState.update {
            it.copy(
                showDeleteDialog = false,
                deletingSubCategoryId = null
            )
        }
    }

    fun createSubCategory(name: String, description: String?) {
        val categoryId = _uiState.value.selectedCategoryId ?: return
        
        viewModelScope.launch {
            _operationState.value = Resource.Loading()
            categoryRepository.createSubCategory(categoryId, name, description).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _operationState.value = Resource.Success("Sub kategori berhasil ditambahkan")
                        hideAddEditDialog()
                        loadSubCategories(categoryId)
                    }
                    is Resource.Error -> {
                        _operationState.value = Resource.Error(resource.message ?: "Gagal menambahkan sub kategori")
                    }
                    is Resource.Loading -> {
                        _operationState.value = Resource.Loading()
                    }
                }
            }
        }
    }

    fun updateSubCategory(id: Long, name: String, description: String?) {
        val categoryId = _uiState.value.selectedCategoryId ?: return
        
        viewModelScope.launch {
            _operationState.value = Resource.Loading()
            categoryRepository.updateSubCategory(id, name, description).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _operationState.value = Resource.Success("Sub kategori berhasil diupdate")
                        hideAddEditDialog()
                        loadSubCategories(categoryId)
                    }
                    is Resource.Error -> {
                        _operationState.value = Resource.Error(resource.message ?: "Gagal mengupdate sub kategori")
                    }
                    is Resource.Loading -> {
                        _operationState.value = Resource.Loading()
                    }
                }
            }
        }
    }

    fun deleteSubCategory() {
        val categoryId = _uiState.value.selectedCategoryId ?: return
        val subCategoryId = _uiState.value.deletingSubCategoryId ?: return
        
        viewModelScope.launch {
            _operationState.value = Resource.Loading()
            categoryRepository.deleteSubCategory(subCategoryId).collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        _operationState.value = Resource.Success("Sub kategori berhasil dihapus")
                        hideDeleteDialog()
                        loadSubCategories(categoryId)
                    }
                    is Resource.Error -> {
                        _operationState.value = Resource.Error(resource.message ?: "Gagal menghapus sub kategori")
                    }
                    is Resource.Loading -> {
                        _operationState.value = Resource.Loading()
                    }
                }
            }
        }
    }

    fun resetOperationState() {
        _operationState.value = null
    }
}
