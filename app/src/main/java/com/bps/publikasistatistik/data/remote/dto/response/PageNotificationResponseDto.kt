package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PageNotificationResponseDto(
    @SerializedName("content")
    val content: List<NotificationResponseDto>,
    
    @SerializedName("totalElements")
    val totalElements: Long,
    
    @SerializedName("totalPages")
    val totalPages: Int,
    
    @SerializedName("size")
    val size: Int,
    
    /** Current page (0-based) */
    @SerializedName("number")
    val number: Int,
    
    @SerializedName("first")
    val first: Boolean,
    
    @SerializedName("last")
    val last: Boolean,
    
    @SerializedName("numberOfElements")
    val numberOfElements: Int
)
