package com.bps.publikasistatistik.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.domain.model.SearchHistory
import com.bps.publikasistatistik.presentation.common.components.BpsSearchBar
import com.bps.publikasistatistik.presentation.home.components.PublicationCard
import com.bps.publikasistatistik.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val filters = viewModel.filterState.value

    // Agar keyboard otomatis muncul saat masuk halaman ini (Opsional, UX lebih baik)
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                // 1. Search Bar (Asli - Bisa Diketik)
                BpsSearchBar(
                    query = filters.query,
                    onQueryChange = viewModel::onQueryChange,
                    modifier = Modifier.focusRequester(focusRequester),
                    placeholder = "Cari judul, kategori...",
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 2. Filter Chips (Horizontal Scroll)
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tombol Reset (Hanya muncul jika ada filter aktif)
                    val isFilterActive = filters.categoryId != null ||
                            filters.year != null ||
                            filters.sort != "latest"

                    if (isFilterActive) {
                        FilterChip(
                            selected = true,
                            onClick = { viewModel.clearFilters() },
                            label = { Text("Reset") },
                            leadingIcon = { Icon(Icons.Default.Close, null) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        )
                    }

                    // Sort Filter
                    FilterChip(
                        selected = filters.sort == "popular",
                        onClick = {
                            val newSort = if (filters.sort == "popular") "latest" else "popular"
                            viewModel.onSortChange(newSort)
                        },
                        label = { Text("Populer") },
                        leadingIcon = { if(filters.sort == "popular") Icon(Icons.Default.Sort, null) else null }
                    )

                    // Tahun Filter (Contoh Hardcoded - Nanti bisa dinamis dari API)
                    val years = listOf(2024, 2023, 2022)
                    years.forEach { y ->
                        FilterChip(
                            selected = filters.year == y,
                            onClick = {
                                val newYear = if (filters.year == y) null else y
                                viewModel.onYearChange(newYear)
                            },
                            label = { Text(y.toString()) }
                        )
                    }

                    // Kategori Filter (Contoh ID 1 & 2 - Nanti bisa dinamis)
                    FilterChip(
                        selected = filters.categoryId == 1L,
                        onClick = {
                            val newCat = if (filters.categoryId == 1L) null else 1L
                            viewModel.onCategoryChange(newCat)
                        },
                        label = { Text("Sosial") }
                    )
                    FilterChip(
                        selected = filters.categoryId == 2L,
                        onClick = {
                            val newCat = if (filters.categoryId == 2L) null else 2L
                            viewModel.onCategoryChange(newCat)
                        },
                        label = { Text("Ekonomi") }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- LOGIKA TAMPILAN KONTEN ---

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            else if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            // Tampilan Awal: RIWAYAT PENCARIAN
            else if (state.isInitial) {
                HistorySection(
                    history = state.searchHistory,
                    onClear = { viewModel.clearHistory() },
                    onItemClick = { keyword -> viewModel.onQueryChange(keyword) }
                )
            }
            // Tampilan Hasil Kosong
            else if (state.publications.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tidak ditemukan hasil", style = MaterialTheme.typography.titleMedium)
                    Text("Coba kata kunci atau filter lain", color = Color.Gray)
                }
            }
            // Tampilan Hasil: LIST PUBLIKASI
            else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.publications) { pub ->
                        PublicationCard(
                            title = pub.title,
                            coverUrl = pub.coverUrl,
                            category = pub.categoryName,
                            year = pub.year,
                            onClick = { navController.navigate(Screen.Detail.createRoute(pub.id)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistorySection(
    history: List<SearchHistory>,
    onClear: () -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pencarian Terakhir",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onClear) {
                    Text("Hapus Semua", color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), contentAlignment = Alignment.Center) {
                Text("Ketik sesuatu untuk mulai mencari", color = Color.Gray)
            }
        } else {
            LazyColumn {
                items(history) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item.keyword) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = item.keyword,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Divider(color = Color.LightGray.copy(alpha = 0.5f))
                }
            }
        }
    }
}