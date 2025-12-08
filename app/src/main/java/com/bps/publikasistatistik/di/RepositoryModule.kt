package com.bps.publikasistatistik.di

import com.bps.publikasistatistik.data.remote.api.AuthApi
import com.bps.publikasistatistik.data.repository.AuthRepositoryImpl
import com.bps.publikasistatistik.domain.repository.AuthRepository
import com.bps.publikasistatistik.data.local.AuthPreferences
import com.bps.publikasistatistik.data.remote.api.CategoryApi
import com.bps.publikasistatistik.data.remote.api.NotificationApi
import com.bps.publikasistatistik.data.remote.api.PublicationApi
import com.bps.publikasistatistik.data.remote.api.SearchHistoryApi
import com.bps.publikasistatistik.data.remote.api.UserApi
import com.bps.publikasistatistik.data.repository.CategoryRepositoryImpl
import com.bps.publikasistatistik.data.repository.NotificationRepositoryImpl
import com.bps.publikasistatistik.data.repository.PublicationRepositoryImpl
import com.bps.publikasistatistik.data.repository.SearchHistoryRepositoryImpl
import com.bps.publikasistatistik.data.repository.UserRepositoryImpl
import com.bps.publikasistatistik.domain.repository.CategoryRepository
import com.bps.publikasistatistik.domain.repository.NotificationRepository
import com.bps.publikasistatistik.domain.repository.PublicationRepository
import com.bps.publikasistatistik.domain.repository.SearchHistoryRepository
import com.bps.publikasistatistik.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: AuthApi,
        preferences: AuthPreferences
    ): AuthRepository {
        return AuthRepositoryImpl(api, preferences)
    }

    @Provides
    @Singleton
    fun providePublicationApi(retrofit: Retrofit): PublicationApi {
        return retrofit.create(PublicationApi::class.java)
    }

    @Provides
    @Singleton
    fun providePublicationRepository(api: PublicationApi): PublicationRepository {
        return PublicationRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideCategoryApi(retrofit: Retrofit): CategoryApi {
        return retrofit.create(CategoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(api: CategoryApi): CategoryRepository {
        return CategoryRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(api: UserApi): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideNotificationApi(retrofit: Retrofit): NotificationApi {
        return retrofit.create(NotificationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(api: NotificationApi): NotificationRepository {
        return NotificationRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryApi(retrofit: Retrofit): SearchHistoryApi {
        return retrofit.create(SearchHistoryApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSearchHistoryRepository(api: SearchHistoryApi): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(api)
    }
}