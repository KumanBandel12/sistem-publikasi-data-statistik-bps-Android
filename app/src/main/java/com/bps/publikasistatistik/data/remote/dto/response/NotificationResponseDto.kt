package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class NotificationResponseDto(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    @SerializedName("read") // JSON backend pake "read", bukan "isRead"
    val isRead: Boolean,
    val createdAt: String?
)