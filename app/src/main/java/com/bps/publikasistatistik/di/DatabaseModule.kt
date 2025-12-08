package com.bps.publikasistatistik.di

import android.content.Context
import androidx.room.Room
import com.bps.publikasistatistik.data.local.AppDatabase
import com.bps.publikasistatistik.data.local.dao.DownloadDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "bps_database"
        ).fallbackToDestructiveMigration() // Reset DB jika ada perubahan struktur (aman untuk dev)
            .build()
    }

    @Provides
    @Singleton
    fun provideDownloadDao(database: AppDatabase): DownloadDao {
        return database.downloadDao()
    }
}