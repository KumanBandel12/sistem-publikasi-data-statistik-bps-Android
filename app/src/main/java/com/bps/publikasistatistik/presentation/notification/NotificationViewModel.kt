package com.bps.publikasistatistik.presentation.notification

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.domain.usecase.notification.GetNotificationsUseCase
import com.bps.publikasistatistik.domain.usecase.notification.MarkNotificationReadUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class NotificationUiState(
    val isLoading: Boolean = false,
    val notifications: List<Notification> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase
) : ViewModel() {

    private val _state = mutableStateOf(NotificationUiState())
    val state: State<NotificationUiState> = _state

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            getNotificationsUseCase().collect { result ->
                when (result) {
                    is Resource.Loading -> _state.value = _state.value.copy(isLoading = true)
                    is Resource.Success -> _state.value = _state.value.copy(
                        isLoading = false,
                        notifications = result.data ?: emptyList()
                    )
                    is Resource.Error -> _state.value = _state.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun markAsRead(notification: Notification) {
        if (notification.isRead) return // Sudah dibaca, skip

        viewModelScope.launch {
            markReadUseCase(notification.id).collect {
                // Update UI secara lokal (Optimistic update)
                if (it is Resource.Success) {
                    val updatedList = _state.value.notifications.map { item ->
                        if (item.id == notification.id) item.copy(isRead = true) else item
                    }
                    _state.value = _state.value.copy(notifications = updatedList)
                }
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            markReadUseCase.all().collect {
                if (it is Resource.Success) loadNotifications() // Reload data
            }
        }
    }
}