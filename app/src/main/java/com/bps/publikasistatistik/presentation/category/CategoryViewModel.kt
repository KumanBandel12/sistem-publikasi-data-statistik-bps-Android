package com.bps.publikasistatistik.presentation.category

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.domain.usecase.category.GetCategoriesUseCase
import com.bps.publikasistatistik.domain.usecase.category.GetSubCategoriesUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getSubCategoriesUseCase: GetSubCategoriesUseCase
) : ViewModel() {

    private val _state = mutableStateOf(CategoryUiState())
    val state: State<CategoryUiState> = _state

    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            getCategoriesUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = CategoryUiState(isLoading = true)
                    is Resource.Success -> _state.value = CategoryUiState(categories = result.data ?: emptyList())
                    is Resource.Error -> _state.value = CategoryUiState(error = result.message)
                }
            }
        }
    }
}