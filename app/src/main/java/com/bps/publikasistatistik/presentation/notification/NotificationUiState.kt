package com.bps.publikasistatistik.presentation.notification

import com.bps.publikasistatistik.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Long = 0,
    val error: String? = null,
    val activeFilter: NotificationFilter = NotificationFilter.ALL
)