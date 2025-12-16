package com.bps.publikasistatistik.presentation.category

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource // IMPORT PENTING
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.presentation.category.components.CategoryCard
import com.bps.publikasistatistik.presentation.home.components.PublicationCard
import com.bps.publikasistatistik.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavController,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    val state = viewModel.state.value

    // Handle Back Press
    BackHandler(enabled = state.isDetailMode) {
        viewModel.onBackToGrid()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isDetailMode) state.selectedParentCategory?.name ?: "Detail" else "Kategori Statistik")
                },
                navigationIcon = {
                    if (state.isDetailMode) {
                        IconButton(onClick = { viewModel.onBackToGrid() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                        }
                    }
                },
                actions = {
                    if (!state.isDetailMode) {
                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                imageVector = if (state.isTreeView) Icons.Default.GridView else Icons.Default.AccountTree,
                                contentDescription = "Ganti Tampilan"
                            )
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
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // 1. MODE DETAIL
            if (state.isDetailMode) {
                Column(modifier = Modifier.fillMaxSize()) {
                    SubCategoryDropdownSelector(
                        subCategories = state.subCategories,
                        selectedSub = state.selectedSubCategory,
                        onSelect = { viewModel.onSelectSubCategory(it) }
                    )
                    Divider()

                    if (state.isPublicationsLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (state.publications.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Tidak ada publikasi di kategori ini", color = MaterialTheme.colorScheme.secondary)
                        }
                    } else {
                        LazyColumn(contentPadding = PaddingValues(16.dp)) {
                            items(state.publications) { pub ->
                                PublicationCard(
                                    title = pub.title,
                                    coverUrl = pub.coverUrl,
                                    category = pub.categoryName,
                                    year = pub.year,
                                    onClick = {
                                        navController.navigate(Screen.Detail.createRoute(pub.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // 2. MODE TREE
            else if (state.isTreeView) {
                if (state.isLoading && state.categoryTree.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    LazyColumn(contentPadding = PaddingValues(16.dp)) {
                        items(state.categoryTree) { category ->
                            CategoryTreeItem(
                                category = category,
                                onLeafClick = { catId ->
                                    navController.navigate("search?categoryId=$catId")
                                }
                            )
                            Divider(color = Color.LightGray.copy(alpha = 0.3f))
                        }
                    }
                }
            }

            // 3. MODE GRID
            else {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.parentCategories) { category ->
                            CategoryCard(
                                name = category.name,
                                count = category.totalPublications,
                                onClick = { viewModel.onSelectParentCategory(category) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryDropdownSelector(
    subCategories: List<Category>,
    selectedSub: Category?,
    onSelect: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedSub?.name ?: "Pilih Sub Kategori",
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Sub Kategori") },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                subCategories.forEach { sub ->
                    DropdownMenuItem(
                        text = { Text(text = sub.name) },
                        onClick = {
                            onSelect(sub)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryTreeItem(
    category: Category,
    onLeafClick: (Long) -> Unit,
    level: Int = 0
) {
    var isExpanded by remember { mutableStateOf(false) }
    val hasChildren = category.subCategories.isNotEmpty()

    // --- FIX CRASH: Buat InteractionSource Manual ---
    val interactionSource = remember { MutableInteractionSource() }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // --- FIX CRASH: Gunakan parameter interactionSource & indication = null ---
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    if (hasChildren) {
                        isExpanded = !isExpanded
                    } else {
                        onLeafClick(category.id)
                    }
                }
                .padding(
                    start = (16 * level).dp,
                    top = 12.dp,
                    bottom = 12.dp,
                    end = 16.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (level == 0) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )

            if (hasChildren) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Column {
                category.subCategories.forEach { child ->
                    CategoryTreeItem(
                        category = child,
                        onLeafClick = onLeafClick,
                        level = level + 1
                    )
                }
            }
        }
    }
}