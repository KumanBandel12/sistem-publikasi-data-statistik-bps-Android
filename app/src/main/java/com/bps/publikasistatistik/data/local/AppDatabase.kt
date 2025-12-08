package com.bps.publikasistatistik.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bps.publikasistatistik.data.local.dao.DownloadDao
import com.bps.publikasistatistik.data.local.entity.DownloadedFileEntity

// Daftarkan entity di sini. Version 1.
@Database(entities = [DownloadedFileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao
}