package com.bps.publikasistatistik.presentation.publication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.presentation.home.HomeTab
import com.bps.publikasistatistik.presentation.home.HomeViewModel
import com.bps.publikasistatistik.presentation.home.components.PublicationCardSkeleton
import com.bps.publikasistatistik.presentation.home.components.PublicationListItem
import com.bps.publikasistatistik.presentation.theme.BackgroundScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPublicationsScreen(
    navController: NavController,
    filter: String, // "latest" or "popular"
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit
) {
    val state = viewModel.state.value
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    
    val title = when (filter) {
        "latest" -> "Publikasi Terbaru"
        "popular" -> "Publikasi Terpopuler"
        else -> "Semua Publikasi"
    }
    
    val publications = when (filter) {
        "latest" -> state.latestPublications
        "popular" -> state.popularPublications
        else -> state.latestPublications
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BackgroundScreen)
        ) {
            if (state.isLoading) {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(10) {
                        PublicationCardSkeleton()
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(publications) { pub ->
                        PublicationListItem(
                            title = pub.title,
                            description = pub.description,
                            coverUrl = pub.coverUrl,
                            category = pub.categoryName,
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
