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

    @Provides
    @Singleton
    fun provideAndroidDownloader(
        @ApplicationContext context: Context,
        authPreferences: AuthPreferences
    ): AndroidDownloader {
        return AndroidDownloader(context, authPreferences)
    }
}