package com.bps.publikasistatistik.presentation.publication.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.local.AuthPreferences
import com.bps.publikasistatistik.data.local.dao.DownloadDao
import com.bps.publikasistatistik.data.local.entity.DownloadedFileEntity
import com.bps.publikasistatistik.data.manager.AndroidDownloader
import com.bps.publikasistatistik.domain.repository.PublicationRepository
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationDetailUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicationDetailViewModel @Inject constructor(
    private val repository: PublicationRepository,
    private val getDetailUseCase: GetPublicationDetailUseCase,
    private val downloader: AndroidDownloader,
    private val downloadDao: DownloadDao, // Inject DAO
    private val authPreferences: AuthPreferences,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(PublicationDetailUiState())
    val state: State<PublicationDetailUiState> = _state

    init {
        // 1. Ambil ID dari navigasi
        savedStateHandle.get<String>("publicationId")?.let { idString ->
            val id = idString.toLongOrNull()
            if (id != null) {
                getPublicationDetail(id)
            }
        }

        // 2. Cek Role User
        checkUserRole()
    }

    private fun checkUserRole() {
        authPreferences.getUserRole().onEach { role ->
            val isAdmin = role.equals("admin", ignoreCase = true)
            _state.value = _state.value.copy(isAdmin = isAdmin)
        }.launchIn(viewModelScope)
    }

    private fun getPublicationDetail(id: Long) {
        viewModelScope.launch {
            getDetailUseCase(id).collect { result ->
                when(result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(isLoading = false, publication = result.data)
                    is Resource.Error -> _state.value = _state.value.copy(isLoading = false, error = result.message)
                }
            }
        }
    }

    fun downloadPublication() {
        val pub = state.value.publication ?: return
        if (pub.fileUrl == null) return

        viewModelScope.launch {
            val fileName = "${pub.title.take(20).replace(" ", "_")}_${pub.id}.pdf"

            // 1. Jalankan Download
            val savedPath = downloader.downloadFile(pub.fileUrl, fileName)

            // 2. Jika sukses ter-enqueue, simpan metadata ke DB
            if (savedPath != null) {
                val entity = DownloadedFileEntity(
                    id = pub.id,
                    title = pub.title,
                    coverUrl = pub.coverUrl,
                    filePath = savedPath,
                    categoryName = pub.categoryName,
                    year = pub.year
                )
                downloadDao.insertDownload(entity)
            }
        }
    }

    fun deletePublication() {
        val pubId = state.value.publication?.id ?: return

        viewModelScope.launch {
            repository.deletePublication(pubId).collect { result ->
                when(result) {
                    is Resource.Loading -> {
                        _state.value = _state.value.copy(isLoading = true)
                    }
                    is Resource.Success -> {
                        _state.value = _state.value.copy(isLoading = false, isDeleted = true)
                    }
                    is Resource.Error -> {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            deleteError = result.message
                        )
                    }
                }
            }
        }
    }
}