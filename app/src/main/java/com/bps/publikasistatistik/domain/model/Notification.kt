package com.bps.publikasistatistik.domain.model

data class Notification(
    val id: Long,
    val title: String,
    val message: String,
    val type: String, // INFO, ALERT, PROMOTION
    val isRead: Boolean,
    val createdAt: String // Kita simpan sebagai String dulu
)