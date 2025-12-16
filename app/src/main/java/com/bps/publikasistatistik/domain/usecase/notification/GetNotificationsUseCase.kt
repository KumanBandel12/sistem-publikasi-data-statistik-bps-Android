package com.bps.publikasistatistik.domain.usecase.notification

import com.bps.publikasistatistik.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationsUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20) = repository.getNotifications(page, size)
}