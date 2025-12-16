package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.NotificationResponseDto
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {
    @GET("notifications")
    suspend fun getNotifications(): Response<ApiResponseDto<List<NotificationResponseDto>>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<ApiResponseDto<Void>>

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResponseDto<Void>>

    @GET("notifications/unread/count")
    suspend fun getUnreadCount(): Response<ApiResponseDto<Long>>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long): Response<ApiResponseDto<Void>>

    @DELETE("notifications/clear-all")
    suspend fun clearAllNotifications(): Response<ApiResponseDto<Void>>

    @GET("notifications/unread")
    suspend fun getUnreadNotifications(): Response<ApiResponseDto<List<NotificationResponseDto>>>
}