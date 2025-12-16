package com.bps.publikasistatistik.domain.usecase.notification

import com.bps.publikasistatistik.domain.repository.NotificationRepository
import javax.inject.Inject

class DeleteNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(id: Long) = repository.deleteNotification(id)
}