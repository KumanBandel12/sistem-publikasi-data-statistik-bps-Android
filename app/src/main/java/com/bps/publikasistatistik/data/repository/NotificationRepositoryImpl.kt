package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.NotificationApi
import com.bps.publikasistatistik.domain.repository.NotificationRepository
import com.bps.publikasistatistik.util.Resource
import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.domain.model.PagedNotifications
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepository {

    override suspend fun getNotifications(page: Int, size: Int): Flow<Resource<PagedNotifications>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getNotifications(page = page, size = size)
            if (response.isSuccessful && response.body()?.success == true) {
                val pageData = response.body()!!.data
                val pagedNotifications = PagedNotifications(
                    notifications = pageData.content.map { it.toDomain() },
                    totalPages = pageData.totalPages,
                    currentPage = pageData.number,
                    totalElements = pageData.totalElements,
                    isLastPage = pageData.last,
                    isFirstPage = pageData.first
                )
                emit(Resource.Success(pagedNotifications))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to load notifications"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun markAsRead(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.markAsRead(id)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to mark as read"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun markAllAsRead(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.markAllAsRead()
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to mark all as read"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getUnreadCount(): Flow<Resource<Int>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUnreadCount()
            if (response.isSuccessful && response.body()?.success == true) {
                val countMap = response.body()!!.data  // Map<String, Long>
                val count = countMap["count"]?.toInt() ?: 0  // Extract "count" from map
                emit(Resource.Success(count))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to get unread count"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun deleteNotification(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteNotification(id)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to delete notification"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun clearAllNotifications(): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.clearAllNotifications()
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Failed to clear notifications"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getUnreadNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getUnreadNotifications()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}