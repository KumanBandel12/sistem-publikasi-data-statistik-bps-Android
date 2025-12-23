package com.bps.publikasistatistik.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.presentation.home.components.*
import com.bps.publikasistatistik.presentation.navigation.BottomNavItem
import com.bps.publikasistatistik.presentation.theme.BackgroundScreen

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
            // New Top Bar with Profile, Greeting, and Notification
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Picture and Greeting
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Profile Picture
                        AsyncImage(
                            model = state.user?.profilePictureUrl ?: "https://ui-avatars.com/api/?name=${state.user?.fullName ?: state.user?.username ?: "User"}&background=random",
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Greeting
                        Column {
                            Text(
                                text = viewModel.getGreeting(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = state.user?.fullName ?: state.user?.username ?: "Pengguna",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "👋",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }
                    }
                    
                    // Notification Bell
                    IconButton(onClick = { navController.navigate("notifications") }) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error
                                ) {
                                    Text("3", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifikasi",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
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
                .background(BackgroundScreen)
        ) {
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                modifier = Modifier.fillMaxSize()
            ) {

                // --- LOGIKA SHIMMER EFFECT ---
                if (state.isLoading && !isRefreshing) {
                    // Welcome Banner
                    item { WelcomeBanner() }
                    
                    // Search Bar
                    item { 
                        SearchBarComponent(onClick = { 
                            navController.navigate(BottomNavItem.Search.route) 
                        })
                    }
                    
                    // Skeleton Featured Publications
                    item { 
                        SectionHeaderNew(
                            title = "Publikasi Unggulan"
                        )
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(3) {
                                CarouselItemSkeleton()
                            }
                        }
                    }
                    
                    // Skeleton Terbaru (Carousel)
                    item { 
                        SectionHeaderNew(
                            title = "Publikasi Terbaru"
                        )
                    }
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(5) {
                                CarouselItemSkeleton()
                            }
                        }
                    }

                    // Skeleton Populer (List)
                    item { 
                        SectionHeaderNew(
                            title = "Publikasi Populer",
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                    items(5) {
                        PublicationCardSkeleton()
                    }

                } else {
                    // --- TAMPILAN DATA ASLI ---
                    
                    // Welcome Banner
                    item { WelcomeBanner() }
                    
                    // Search Bar
                    item { 
                        SearchBarComponent(onClick = { 
                            navController.navigate(BottomNavItem.Search.route) 
                        })
                    }
                    
                    // Featured Publications Carousel
                    if (state.featuredPublications.isNotEmpty()) {
                        item {
                            FeaturedPublicationsSection(
                                publications = state.featuredPublications,
                                onPublicationClick = onNavigateToDetail
                            )
                        }
                    }

                    // SECTION: TERBARU (Horizontal Scroll)
                    if (state.latestPublications.isNotEmpty()) {
                        item {
                            SectionHeaderNew(
                                title = "Publikasi Terbaru",
                                onSeeAllClick = {
                                    navController.navigate("all_publications/latest")
                                }
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(state.latestPublications) { pub ->
                                    PublicationCardHorizontal(
                                        title = pub.title,
                                        description = pub.description,
                                        coverUrl = pub.coverUrl,
                                        category = pub.subCategoryName,
                                        year = pub.year,
                                        views = pub.views,
                                        downloads = pub.downloads,
                                        onClick = { onNavigateToDetail(pub.id) }
                                    )
                                }
                            }
                        }
                    }
                    
                    // SECTION: POPULER (Vertical List)
                    if (state.popularPublications.isNotEmpty()) {
                        item {
                            SectionHeaderNew(
                                title = "Publikasi Populer",
                                onSeeAllClick = {
                                    navController.navigate("all_publications/popular")
                                }
                            )
                        }
                        items(state.popularPublications) { pub ->
                            PublicationListItem(
                                title = pub.title,
                                description = pub.description,
                                coverUrl = pub.coverUrl,
                                category = pub.subCategoryName,
                                year = pub.year,
                                views = pub.views,
                                downloads = pub.downloads,
                                onClick = { onNavigateToDetail(pub.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
