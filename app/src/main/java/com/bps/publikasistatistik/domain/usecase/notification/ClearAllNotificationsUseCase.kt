package com.bps.publikasistatistik.domain.usecase.notification

import com.bps.publikasistatistik.domain.repository.NotificationRepository
import javax.inject.Inject

class ClearAllNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke() = repository.clearAllNotifications()
}