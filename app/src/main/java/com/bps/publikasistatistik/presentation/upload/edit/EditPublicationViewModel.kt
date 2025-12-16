package com.bps.publikasistatistik.presentation.publication.edit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationDetailUseCase
import com.bps.publikasistatistik.domain.repository.PublicationRepository
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditPublicationViewModel @Inject constructor(
    private val repository: PublicationRepository,
    private val getDetailUseCase: GetPublicationDetailUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Simpan ID Publikasi
    private val publicationId: Long = savedStateHandle.get<String>("publicationId")?.toLongOrNull() ?: -1L

    // State Form
    var title = mutableStateOf("")
    var description = mutableStateOf("")
    var year = mutableStateOf("")
    var categoryId = mutableStateOf("")

    // State UI
    private val _editState = MutableStateFlow<Resource<String>?>(null)
    val editState = _editState.asStateFlow()

    init {
        if (publicationId != -1L) {
            loadCurrentData(publicationId)
        }
    }

    private fun loadCurrentData(id: Long) {
        viewModelScope.launch {
            getDetailUseCase(id).collect { result ->
                if (result is Resource.Success) {
                    val data = result.data
                    if (data != null) {
                        title.value = data.title
                        description.value = data.description ?: ""
                        year.value = data.year.toString()
                        // Karena di model Publication mungkin categoryId tidak terekspos langsung (hanya categoryName),
                        // kita asumsi user harus input ulang ID atau kita ambil dari entity jika ada.
                        // Untuk sekarang biarkan kosong agar user isi/edit, atau default ke 1.
                        categoryId.value = "1"
                    }
                }
            }
        }
    }

    fun updatePublication() {
        if (publicationId == -1L) return

        viewModelScope.launch {
            _editState.value = Resource.Loading()

            val request = PublicationRequestDto(
                title = title.value,
                description = description.value,
                year = year.value.toIntOrNull() ?: 2024,
                categoryId = categoryId.value.toLongOrNull() ?: 1,
                author = "Admin"
            )

            repository.updatePublication(publicationId, request).collect { result ->
                when (result) {
                    is Resource.Loading -> _editState.value = Resource.Loading()
                    is Resource.Success -> _editState.value = Resource.Success("Update Berhasil")
                    is Resource.Error -> _editState.value = Resource.Error(result.message ?: "Gagal")
                }
            }
        }
    }
}