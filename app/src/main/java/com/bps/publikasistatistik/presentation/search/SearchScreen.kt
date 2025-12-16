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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalFocusManager // 1. IMPORT INI
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.domain.model.SearchHistory
import com.bps.publikasistatistik.presentation.common.components.BpsSearchBar
import com.bps.publikasistatistik.presentation.home.components.PublicationCard
import com.bps.publikasistatistik.presentation.home.components.PublicationCardSkeleton
import com.bps.publikasistatistik.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val filters = viewModel.filterState.value

    // 2. DEFINISIKAN FOCUS MANAGER
    val focusManager = LocalFocusManager.current

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        // Fokus hanya jika query kosong (baru masuk)
        if (state.query.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                // 3. SEARCH BAR
                BpsSearchBar(
                    query = filters.query,
                    onQueryChange = viewModel::onQueryChange,
                    modifier = Modifier.focusRequester(focusRequester),
                    placeholder = "Cari judul, kategori...",
                    // Tambahkan onSearch untuk handle Enter di keyboard
                    onSearch = {
                        focusManager.clearFocus() // Tutup keyboard
                        viewModel.searchPublications()
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 4. FILTER CHIPS
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val isFilterActive = filters.categoryId != null ||
                            filters.year != null ||
                            filters.sort != "latest"

                    if (isFilterActive) {
                        FilterChip(
                            selected = true,
                            onClick = {
                                focusManager.clearFocus() // Tutup keyboard saat reset
                                viewModel.clearFilters()
                            },
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
                            focusManager.clearFocus() // Tutup keyboard
                            val newSort = if (filters.sort == "popular") "latest" else "popular"
                            viewModel.onSortChange(newSort)
                        },
                        label = { Text("Populer") },
                        leadingIcon = { if(filters.sort == "popular") Icon(Icons.Default.Sort, null) else null }
                    )

                    // Tahun Filter
                    val years = listOf(2024, 2023, 2022)
                    years.forEach { y ->
                        FilterChip(
                            selected = filters.year == y,
                            onClick = {
                                focusManager.clearFocus() // Tutup keyboard
                                val newYear = if (filters.year == y) null else y
                                viewModel.onYearChange(newYear)
                            },
                            label = { Text(y.toString()) }
                        )
                    }

                    // Kategori Filter
                    FilterChip(
                        selected = filters.categoryId == 1L,
                        onClick = {
                            focusManager.clearFocus() // Tutup keyboard
                            val newCat = if (filters.categoryId == 1L) null else 1L
                            viewModel.onCategoryChange(newCat)
                        },
                        label = { Text("Sosial") }
                    )
                    FilterChip(
                        selected = filters.categoryId == 2L,
                        onClick = {
                            focusManager.clearFocus() // Tutup keyboard
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

            if (state.isLoading) {
                // Tampilkan 6 Skeleton Item
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(6) {
                        PublicationCardSkeleton()
                    }
                }
            }

            // 5. SUGGESTION (AUTOCOMPLETE)
            else if (state.suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.suggestions) { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    focusManager.clearFocus() // Tutup Keyboard
                                    viewModel.onSuggestionClick(suggestion)
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = suggestion,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }

            else if (state.error != null) {
                Text(text = state.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
            }

            // 6. HISTORY PENCARIAN
            else if (state.isInitial) {
                HistorySection(
                    history = state.searchHistory,
                    onClear = { viewModel.clearHistory() },
                    onItemClick = { keyword ->
                        focusManager.clearFocus() // Tutup Keyboard
                        viewModel.onSuggestionClick(keyword)
                    }
                )
            }

            else if (state.searchResults.isEmpty()) {
                Text("Tidak ditemukan", modifier = Modifier.align(Alignment.Center))
            }

            // 7. HASIL PENCARIAN
            else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.searchResults) { pub ->
                        PublicationCard(
                            title = pub.title,
                            coverUrl = pub.coverUrl,
                            category = pub.categoryName,
                            year = pub.year,
                            onClick = {
                                focusManager.clearFocus() // Tutup Keyboard sebelum navigasi
                                navController.navigate(Screen.Detail.createRoute(pub.id))
                            }
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
                            .clickable { onItemClick(item.keyword) } // Handler sudah handle clearFocus
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