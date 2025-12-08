package com.bps.publikasistatistik.data.mapper

import com.bps.publikasistatistik.data.remote.dto.response.NotificationResponseDto
import com.bps.publikasistatistik.domain.model.Notification

fun NotificationResponseDto.toDomain(): Notification {
    return Notification(
        id = id,
        title = title,
        message = message,
        type = type,
        isRead = isRead,
        createdAt = createdAt ?: ""
    )
}