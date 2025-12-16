package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.NotificationResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.PageNotificationResponseDto
import retrofit2.Response
import retrofit2.http.*

interface NotificationApi {
    // FIX 1: Add pagination parameters and return paginated response
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponseDto<PageNotificationResponseDto>>

    @PUT("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: Long): Response<ApiResponseDto<Void>>

    @PUT("notifications/read-all")
    suspend fun markAllAsRead(): Response<ApiResponseDto<Void>>

    // FIX 2: Return Map<String, Long> instead of Long
    @GET("notifications/unread/count")
    suspend fun getUnreadCount(): Response<ApiResponseDto<Map<String, Long>>>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: Long): Response<ApiResponseDto<Void>>

    @DELETE("notifications/clear-all")
    suspend fun clearAllNotifications(): Response<ApiResponseDto<Void>>

    @GET("notifications/unread")
    suspend fun getUnreadNotifications(): Response<ApiResponseDto<List<NotificationResponseDto>>>
}