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

    private fun loadData(page: Int = 0, loadMore: Boolean = false) {
        viewModelScope.launch {
            // Set loading state
            if (loadMore) {
                _state.value = _state.value.copy(isLoadingMore = true)
            } else {
                _state.value = _state.value.copy(isLoading = true)
            }

            // Load unread count
            launch {
                getUnreadCountUseCase().collect { result ->
                    if (result is Resource.Success) {
                        _state.value = _state.value.copy(
                            unreadCount = result.data?.toInt() ?: 0
                        )
                    }
                }
            }

            // Load notifications based on filter
            launch {
                if (_state.value.activeFilter == NotificationFilter.ALL) {
                    // Paginated notifications
                    getNotificationsUseCase(page = page, size = 20).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                val pagedData = result.data
                                if (pagedData != null) {
                                    val updatedList = if (loadMore) {
                                        _state.value.notifications + pagedData.notifications
                                    } else {
                                        pagedData.notifications
                                    }
                                    
                                    _state.value = _state.value.copy(
                                        notifications = updatedList,
                                        currentPage = pagedData.currentPage,
                                        totalPages = pagedData.totalPages,
                                        isLastPage = pagedData.isLastPage,
                                        canLoadMore = !pagedData.isLastPage,
                                        isLoading = false,
                                        isLoadingMore = false,
                                        error = null
                                    )
                                }
                            }
                            is Resource.Error -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    error = result.message
                                )
                            }
                            is Resource.Loading -> {
                                // Already handled above
                            }
                        }
                    }
                } else {
                    // Unread notifications (non-paginated)
                    getUnreadNotificationsUseCase().collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                _state.value = _state.value.copy(
                                    notifications = result.data ?: emptyList(),
                                    isLoading = false,
                                    isLoadingMore = false,
                                    canLoadMore = false,
                                    error = null
                                )
                            }
                            is Resource.Error -> {
                                _state.value = _state.value.copy(
                                    isLoading = false,
                                    isLoadingMore = false,
                                    error = result.message
                                )
                            }
                            is Resource.Loading -> {
                                // Already handled above
                            }
                        }
                    }
                }
            }
        }
    }

    fun loadMoreNotifications() {
        // Only load more if we're on ALL filter and can load more
        val shouldLoad = _state.value.activeFilter == NotificationFilter.ALL 
            && _state.value.canLoadMore 
            && !_state.value.isLoadingMore
        
        if (shouldLoad) {
            val nextPage = _state.value.currentPage + 1
            loadData(page = nextPage, loadMore = true)
        }
    }

    fun refresh() {
        // Reset to first page
        _state.value = _state.value.copy(
            currentPage = 0,
            canLoadMore = true
        )
        loadData(page = 0, loadMore = false)
    }

    fun setFilter(filter: NotificationFilter) {
        _state.value = _state.value.copy(
            activeFilter = filter,
            currentPage = 0,
            canLoadMore = true
        )
        loadData(page = 0, loadMore = false)
    }

    fun markAsRead(notification: Notification) {
        if (notification.isRead) return

        viewModelScope.launch {
            markReadUseCase(notification.id).collect {
                if (it is Resource.Success) {
                    // Update list locally
                    val updatedList = _state.value.notifications.map { item ->
                        if (item.id == notification.id) item.copy(isRead = true) else item
                    }

                    // Decrease unread count manually for responsive UI
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
                    // Reload data to sync with server
                    refresh()
                }
            }
        }
    }

    fun clearAllNotifications() {
        viewModelScope.launch {
            clearAllUseCase().collect { result ->
                if (result is Resource.Success) {
                    // Update UI: Clear list and reset count
                    _state.value = _state.value.copy(
                        notifications = emptyList(),
                        unreadCount = 0
                    )
                }
            }
        }
    }
}