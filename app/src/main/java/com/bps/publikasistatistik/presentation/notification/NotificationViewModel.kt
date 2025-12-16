package com.bps.publikasistatistik.presentation.notification

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.domain.usecase.notification.ClearAllNotificationsUseCase
import com.bps.publikasistatistik.domain.usecase.notification.DeleteNotificationUseCase
import com.bps.publikasistatistik.domain.usecase.notification.GetNotificationsUseCase
import com.bps.publikasistatistik.domain.usecase.notification.GetUnreadCountUseCase
import com.bps.publikasistatistik.domain.usecase.notification.MarkNotificationReadUseCase
import com.bps.publikasistatistik.domain.usecase.notification.GetUnreadNotificationsUseCase
import com.bps.publikasistatistik.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class NotificationFilter { ALL, UNREAD }

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val getNotificationsUseCase: GetNotificationsUseCase,
    private val getUnreadCountUseCase: GetUnreadCountUseCase,
    private val markReadUseCase: MarkNotificationReadUseCase,
    private val deleteUseCase: DeleteNotificationUseCase,
    private val clearAllUseCase: ClearAllNotificationsUseCase,
    private val getUnreadNotificationsUseCase: GetUnreadNotificationsUseCase,
) : ViewModel() {

    private val _state = mutableStateOf(NotificationUiState())
    val state: State<NotificationUiState> = _state

    init {
        loadData()
    }

    // Ubah jadi public jika perlu dipanggil dari luar,
    // tapi karena dipanggil setFilter (yang public), private oke.
    private fun loadData() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            // Cek filter aktif untuk menentukan UseCase mana yang dipakai
            val flow = if (_state.value.activeFilter == NotificationFilter.ALL) {
                getNotificationsUseCase()
            } else {
                getUnreadNotificationsUseCase()
            }

            // 1. Ambil Jumlah Belum Dibaca (Badge) - Tetap dijalankan
            launch {
                getUnreadCountUseCase().collect { result ->
                    if (result is Resource.Success) {
                        _state.value = _state.value.copy(
                            unreadCount = result.data ?: 0L
                        )
                    }
                }
            }

            // 2. Ambil List Notifikasi (Sesuai Filter)
            launch {
                flow.collect { result ->
                    if (result is Resource.Success) {
                        _state.value = _state.value.copy(
                            notifications = result.data ?: emptyList(),
                            isLoading = false
                        )
                    } else if (result is Resource.Error) {
                        _state.value = _state.value.copy(isLoading = false, error = result.message)
                    }
                }
            }
        }
    }

    fun setFilter(filter: NotificationFilter) {
        _state.value = _state.value.copy(activeFilter = filter)
        loadData() // Reload data sesuai filter
    }

    fun markAsRead(notification: Notification) {
        if (notification.isRead) return

        viewModelScope.launch {
            markReadUseCase(notification.id).collect {
                if (it is Resource.Success) {
                    // Update list lokal
                    val updatedList = _state.value.notifications.map { item ->
                        if (item.id == notification.id) item.copy(isRead = true) else item
                    }

                    // Kurangi unread count secara manual agar UI responsif
                    val currentCount = _state.value.unreadCount
                    val newCount = if (currentCount > 0) currentCount - 1 else 0

                    _state.value = _state.value.copy(
                        notifications = updatedList,
                        unreadCount = newCount
                    )
                }
            }
        }
    }

    fun markAllRead() {
        viewModelScope.launch {
            markReadUseCase.all().collect {
                if (it is Resource.Success) {
                    // Reload data agar sinkron dengan server
                    loadData()
                }
            }
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            clearAllUseCase().collect { result ->
                if (result is Resource.Success) {
                    // Update UI: Kosongkan list dan reset count
                    _state.value = _state.value.copy(
                        notifications = emptyList(),
                        unreadCount = 0
                    )
                }
            }
        }
    }
}