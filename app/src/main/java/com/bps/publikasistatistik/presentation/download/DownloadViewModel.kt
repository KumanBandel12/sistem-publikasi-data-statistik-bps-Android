package com.bps.publikasistatistik.presentation.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.data.local.dao.DownloadDao
import com.bps.publikasistatistik.data.local.entity.DownloadedFileEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadDao: DownloadDao
) : ViewModel() {

    // Mengambil data real-time dari database
    // stateIn mengubah Flow menjadi StateFlow agar efisien di Compose
    val downloads: StateFlow<List<DownloadedFileEntity>> = downloadDao.getAllDownloads()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteFile(fileEntity: DownloadedFileEntity) {
        viewModelScope.launch {
            // 1. Hapus file fisik di HP
            try {
                val file = File(fileEntity.filePath)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 2. Hapus data dari Database
            downloadDao.deleteDownload(fileEntity)
        }
    }
}