package com.bps.publikasistatistik.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.presentation.home.components.CarouselItemSkeleton // Import Skeleton
import com.bps.publikasistatistik.presentation.home.components.PublicationCardSkeleton // Import Skeleton
import com.bps.publikasistatistik.presentation.home.components.PublicationCard
import com.bps.publikasistatistik.presentation.navigation.BottomNavItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit
) {
    val state = viewModel.state.value
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            // Header Search & Notif (Tetap sama)
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BPS Publikasi",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        Icon(Icons.Default.Notifications, "Notifikasi", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable { navController.navigate(BottomNavItem.Search.route) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, null, tint = Color.Gray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Cari data statistik...", color = Color.Gray)
                }
            }
        }
    ) { padding ->

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshData() },
            state = pullRefreshState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                // --- LOGIKA SHIMMER EFFECT ---
                if (state.isLoading && !isRefreshing) {
                    // Tampilkan Skeleton Loading

                    // 1. Skeleton Terbaru (Carousel)
                    item { SectionHeader(title = "Publikasi Terbaru") }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(5) { // Tampilkan 5 dummy skeleton
                                CarouselItemSkeleton()
                            }
                        }
                    }

                    // 2. Skeleton Terpopuler (List)
                    item { SectionHeader(title = "Terpopuler", modifier = Modifier.padding(top = 16.dp)) }
                    items(5) { // Tampilkan 5 dummy skeleton
                        PublicationCardSkeleton()
                    }

                } else {
                    // --- TAMPILAN DATA ASLI ---

                    // SECTION 1: TERBARU
                    if (state.latestPublications.isNotEmpty()) {
                        item { SectionHeader(title = "Publikasi Terbaru") }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.latestPublications) { pub ->
                                    CarouselItem(publication = pub, onClick = { onNavigateToDetail(pub.id) })
                                }
                            }
                        }
                    }

                    // SECTION 2: TERPOPULER
                    if (state.popularPublications.isNotEmpty()) {
                        item {
                            SectionHeader(title = "Terpopuler", modifier = Modifier.padding(top = 16.dp))
                        }
                        items(state.popularPublications) { pub ->
                            PublicationCard(
                                title = pub.title,
                                coverUrl = pub.coverUrl,
                                category = pub.categoryName,
                                year = pub.year,
                                onClick = { onNavigateToDetail(pub.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarouselItem(
    publication: Publication,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(140.dp)
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = publication.coverUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = publication.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${publication.categoryName} • ${publication.year}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}