package com.bps.publikasistatistik.domain.model

data class PagedNotifications(
    val notifications: List<Notification>,
    val totalPages: Int,
    val currentPage: Int,
    val totalElements: Long,
    val isLastPage: Boolean,
    val isFirstPage: Boolean
)
