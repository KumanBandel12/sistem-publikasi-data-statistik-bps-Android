package com.bps.publikasistatistik.presentation.publication.detail

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.bps.publikasistatistik.presentation.home.components.PublicationDetailSkeleton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationDetailScreen(
    navController: NavController,
    viewModel: PublicationDetailViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val publication = state.publication

    val context = LocalContext.current

    // State untuk Dialog Konfirmasi Hapus
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Efek jika berhasil dihapus -> Kembali ke halaman sebelumnya
    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            Toast.makeText(context, "Publikasi berhasil dihapus", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // Efek jika gagal hapus
    LaunchedEffect(state.deleteError) {
        if (state.deleteError != null) {
            Toast.makeText(context, state.deleteError, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Publikasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    // Tombol EDIT
                    IconButton(onClick = {
                        // Navigasi ke Edit Screen dengan ID
                        state.publication?.id?.let { id ->
                            navController.navigate("edit_publication/$id")
                        }
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Data", tint = Color.White)
                    }

                    // Tombol HAPUS
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Data", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (state.isLoading) {
                PublicationDetailSkeleton()
            }

            if (state.error != null) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            if (publication != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Gambar Cover Besar
                    AsyncImage(
                        model = publication.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.FillWidth, // Sesuaikan lebar layar
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )

                    Column(modifier = Modifier.padding(16.dp)) {
                        // 2. Judul & Kategori
                        Text(
                            text = publication.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        SuggestionChip(
                            onClick = { },
                            label = { Text(publication.categoryName) }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 3. Info Detail (Grid sederhana)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            DetailItem("Tahun", publication.year.toString())
                            DetailItem("Ukuran", publication.size)
                            DetailItem("Dilihat", "${publication.views}x")
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp))

                        // 4. Deskripsi
                        Text(
                            text = "Deskripsi",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = publication.description ?: "Tidak ada deskripsi",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5. Tombol Download
                        Button(
                            onClick = {
                                viewModel.downloadPublication()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Download PDF")
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Publikasi?") },
            text = { Text("Data yang dihapus tidak dapat dikembalikan. Apakah Anda yakin?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deletePublication() // Action Hapus
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}