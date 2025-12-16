package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.domain.model.PagedNotifications
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    // Update: Add pagination parameters and return PagedNotifications
    suspend fun getNotifications(page: Int = 0, size: Int = 20): Flow<Resource<PagedNotifications>>
    
    suspend fun getUnreadNotifications(): Flow<Resource<List<Notification>>>
    
    // Update: Return Int instead of expecting direct count
    suspend fun getUnreadCount(): Flow<Resource<Int>>
    
    suspend fun markAsRead(id: Long): Flow<Resource<Unit>>
    
    suspend fun markAllAsRead(): Flow<Resource<Unit>>
    
    suspend fun deleteNotification(id: Long): Flow<Resource<Unit>>
    
    suspend fun clearAllNotifications(): Flow<Resource<Unit>>
}