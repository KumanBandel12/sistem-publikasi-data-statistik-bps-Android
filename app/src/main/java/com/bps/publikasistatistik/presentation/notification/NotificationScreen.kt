package com.bps.publikasistatistik.presentation.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.domain.model.Notification
import com.bps.publikasistatistik.presentation.home.components.NotificationItemSkeleton
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    navController: NavController,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val listState = rememberLazyListState()
    
    // Threshold for triggering load more (items from bottom)
    val loadMoreThreshold = 3
    
    // Detect when user scrolls to the bottom
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem != null && lastVisibleItem.index >= totalItems - loadMoreThreshold && totalItems > 0
        }
    }
    
    // Trigger load more when scrolled to bottom
    LaunchedEffect(listState) {
        snapshotFlow { shouldLoadMore.value }
            .distinctUntilChanged()
            .collect { shouldLoad ->
                if (shouldLoad && state.canLoadMore && !state.isLoadingMore && !state.isLoading) {
                    viewModel.loadMoreNotifications()
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifikasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol Baca Semua & Hapus Semua (Hanya muncul jika list tidak kosong)
                    if (state.notifications.isNotEmpty()) {
                        IconButton(onClick = { viewModel.markAllRead() }) {
                            Icon(Icons.Default.DoneAll, contentDescription = "Baca Semua")
                        }
                        IconButton(onClick = { viewModel.clearAllNotifications() }) {
                            Icon(Icons.Default.DeleteSweep, contentDescription = "Hapus Semua")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // --- 1. TAB FILTER (Semua | Belum Dibaca) ---
            TabRow(
                selectedTabIndex = if (state.activeFilter == NotificationFilter.ALL) 0 else 1,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = state.activeFilter == NotificationFilter.ALL,
                    onClick = { viewModel.setFilter(NotificationFilter.ALL) },
                    text = { Text("Semua") }
                )
                Tab(
                    selected = state.activeFilter == NotificationFilter.UNREAD,
                    onClick = { viewModel.setFilter(NotificationFilter.UNREAD) },
                    text = {
                        if (state.unreadCount > 0) {
                            Text("Belum Dibaca (${state.unreadCount})")
                        } else {
                            Text("Belum Dibaca")
                        }
                    }
                )
            }

            // --- 2. KONTEN LIST ---
            Box(modifier = Modifier.fillMaxSize()) {

                // Loading State (initial load)
                if (state.isLoading && state.notifications.isEmpty()) {
                    LazyColumn {
                        items(8) { NotificationItemSkeleton() }
                    }
                }

                // Empty State
                else if (state.notifications.isEmpty() && !state.isLoading) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Different message depending on filter
                        val emptyMessage = if (state.activeFilter == NotificationFilter.UNREAD)
                            "Tidak ada pesan belum dibaca"
                        else
                            "Belum ada notifikasi"

                        Text(emptyMessage, color = Color.Gray)
                    }
                }

                // List Data
                else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(state.notifications) { item ->
                            NotificationItem(
                                notification = item,
                                onClick = { viewModel.markAsRead(item) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                        
                        // Loading indicator at the bottom when loading more
                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            // Warna latar berbeda jika sudah dibaca vs belum
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 1.dp else 4.dp
        )
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (notification.isRead) Color.Gray else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notification.isRead) Color.Gray else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (notification.createdAt.length >= 10) notification.createdAt.take(10) else notification.createdAt,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}