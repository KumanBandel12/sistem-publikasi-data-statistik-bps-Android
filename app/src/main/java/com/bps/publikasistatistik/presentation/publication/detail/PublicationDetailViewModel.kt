package com.bps.publikasistatistik.presentation.publication.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.local.dao.DownloadDao
import com.bps.publikasistatistik.data.local.entity.DownloadedFileEntity
import com.bps.publikasistatistik.data.manager.AndroidDownloader
import com.bps.publikasistatistik.domain.usecase.publication.GetPublicationDetailUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicationDetailViewModel @Inject constructor(
    private val getDetailUseCase: GetPublicationDetailUseCase,
    private val downloader: AndroidDownloader,
    private val downloadDao: DownloadDao, // Inject DAO
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = mutableStateOf(PublicationDetailUiState())
    val state: State<PublicationDetailUiState> = _state

    init {
        val id = savedStateHandle.get<String>("publicationId")?.toLongOrNull()
        if (id != null) getPublicationDetail(id)
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
}