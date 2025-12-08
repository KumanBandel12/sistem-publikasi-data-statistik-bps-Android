package com.bps.publikasistatistik.di

import android.content.Context
import com.bps.publikasistatistik.data.local.AuthPreferences
import com.bps.publikasistatistik.data.manager.AndroidDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Provide Context & AuthPreferences sudah ada (biasanya implicit),
    // tapi kita buat eksplisit provider untuk Downloader saja.

    @Provides
    @Singleton
    fun provideAndroidDownloader(
        @ApplicationContext context: Context,
        authPreferences: AuthPreferences
    ): AndroidDownloader {
        return AndroidDownloader(context, authPreferences)
    }
}