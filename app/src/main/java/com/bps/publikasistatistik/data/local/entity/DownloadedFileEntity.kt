package com.bps.publikasistatistik.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_files")
data class DownloadedFileEntity(
    @PrimaryKey val id: Long, // ID Publikasi
    val title: String,
    val coverUrl: String?,
    val filePath: String, // Lokasi penyimpanan di HP
    val categoryName: String,
    val year: Int,
    val downloadedAt: Long = System.currentTimeMillis()
)