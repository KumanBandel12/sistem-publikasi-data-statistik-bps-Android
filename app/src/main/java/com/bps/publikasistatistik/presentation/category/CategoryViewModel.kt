package com.bps.publikasistatistik.presentation.category

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.domain.usecase.category.GetCategoriesUseCase
import com.bps.publikasistatistik.domain.usecase.category.GetCategoryTreeUseCase
import com.bps.publikasistatistik.domain.usecase.category.GetSubCategoriesUseCase
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationsUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getSubCategoriesUseCase: GetSubCategoriesUseCase,
    private val getPublicationsUseCase: GetPublicationsUseCase,
    private val getCategoryTreeUseCase: GetCategoryTreeUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CategoryUiState())
    val state: State<CategoryUiState> = _state

    init {
        loadParentCategories()
    }

    // 1. Load Kategori Induk (Awal)
    private fun loadParentCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        parentCategories = result.data ?: emptyList()
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun loadCategoryTree() {
        // Cek dulu biar gak load berkali-kali kalau data sudah ada
        if (_state.value.categoryTree.isNotEmpty()) {
            _state.value = _state.value.copy(isTreeView = true)
            return
        }

        viewModelScope.launch {
            getCategoryTreeUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            categoryTree = result.data ?: emptyList(),
                            isTreeView = true // Aktifkan mode tree
                        )
                    }
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun toggleViewMode() {
        if (_state.value.isTreeView) {
            // Kembali ke Grid
            _state.value = _state.value.copy(isTreeView = false)
        } else {
            // Pindah ke Tree
            loadCategoryTree()
        }
    }

    // 2. User Memilih Kategori Induk -> Masuk Mode Detail
    fun onSelectParentCategory(category: Category) {
        _state.value = _state.value.copy(
            isDetailMode = true,
            selectedParentCategory = category,
            isLoading = true // Loading sebentar saat ambil sub-kategori
        )

        viewModelScope.launch {
            // Ambil Sub Kategori
            getSubCategoriesUseCase(category.id).collect { result ->
                if (result is Resource.Success) {
                    val subs = result.data ?: emptyList()

                    // Tambahkan opsi "Semua [Nama Kategori]" di paling atas list sub
                    val allOption = Category(
                        id = category.id, // ID sama dengan parent
                        name = "Semua ${category.name}",
                        description = "",
                        totalPublications = 0 // Dummy
                    )
                    val fullSubs = listOf(allOption) + subs

                    _state.value = _state.value.copy(
                        isLoading = false,
                        subCategories = fullSubs,
                        selectedSubCategory = allOption // Default pilih "Semua"
                    )

                    // Langsung load publikasi untuk pilihan default ("Semua")
                    loadPublications(category.id)
                } else if (result is Resource.Error) {
                    _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    // 3. User Memilih Sub Kategori dari Dropdown
    fun onSelectSubCategory(subCategory: Category) {
        _state.value = _state.value.copy(selectedSubCategory = subCategory)
        // Load publikasi berdasarkan ID sub kategori yang dipilih
        loadPublications(subCategory.id)
    }

    // Helper: Load Publikasi
    private fun loadPublications(categoryId: Long) {
        viewModelScope.launch {
            // Kita pakai sort="latest" sebagai default
            getPublicationsUseCase(categoryId = categoryId, sort = "latest").collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isPublicationsLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isPublicationsLoading = false,
                        publications = result.data ?: emptyList()
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isPublicationsLoading = false,
                        error = result.message // Tampilkan error di list, bukan global screen
                    )
                }
            }
        }
    }

    // 4. Tombol Kembali (Back) -> Balik ke Grid
    fun onBackToGrid() {
        _state.value = _state.value.copy(
            isDetailMode = false,
            selectedParentCategory = null,
            subCategories = emptyList(),
            publications = emptyList(),
            error = null
        )
    }
}