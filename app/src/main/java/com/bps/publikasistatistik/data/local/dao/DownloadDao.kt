package com.bps.publikasistatistik.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bps.publikasistatistik.data.local.entity.DownloadedFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {
    // Ambil semua download, urutkan dari yang terbaru
    @Query("SELECT * FROM downloaded_files ORDER BY downloadedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadedFileEntity>>

    // Cek apakah buku ini sudah ada di database
    @Query("SELECT EXISTS(SELECT * FROM downloaded_files WHERE id = :id)")
    suspend fun isDownloaded(id: Long): Boolean

    // Simpan data download (kalau sudah ada, timpa)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(file: DownloadedFileEntity)

    // Hapus data download
    @Delete
    suspend fun deleteDownload(file: DownloadedFileEntity)
}