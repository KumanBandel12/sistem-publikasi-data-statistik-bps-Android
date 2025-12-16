package com.bps.publikasistatistik.presentation.notification

import com.bps.publikasistatistik.domain.model.Notification

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val unreadCount: Int = 0,
    val error: String? = null,
    val activeFilter: NotificationFilter = NotificationFilter.ALL,
    
    // Pagination fields
    val currentPage: Int = 0,
    val totalPages: Int = 0,
    val isLastPage: Boolean = false,
    val isLoadingMore: Boolean = false,
    val canLoadMore: Boolean = true
)