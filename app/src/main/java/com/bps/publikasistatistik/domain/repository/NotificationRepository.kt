package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    suspend fun getNotifications(): Flow<Resource<List<Notification>>>
    suspend fun markAsRead(id: Long): Flow<Resource<Boolean>>
    suspend fun markAllAsRead(): Flow<Resource<Boolean>>
    suspend fun getUnreadCount(): Flow<Resource<Long>>
    suspend fun deleteNotification(id: Long): Flow<Resource<Boolean>>
    suspend fun clearAllNotifications(): Flow<Resource<Boolean>>
    suspend fun getUnreadNotifications(): Flow<Resource<List<Notification>>>
}