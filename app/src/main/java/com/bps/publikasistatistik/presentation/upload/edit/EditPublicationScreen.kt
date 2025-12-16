package com.bps.publikasistatistik.presentation.publication.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bps.publikasistatistik.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPublicationScreen(
    navController: NavController,
    viewModel: EditPublicationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val editState by viewModel.editState.collectAsState()

    LaunchedEffect(editState) {
        when (val result = editState) {
            is Resource.Success -> {
                Toast.makeText(context, "Data berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                navController.popBackStack() // Kembali ke Detail
            }
            is Resource.Error -> {
                Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Publikasi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Judul
            OutlinedTextField(
                value = viewModel.title.value,
                onValueChange = { viewModel.title.value = it },
                label = { Text("Judul Publikasi") },
                modifier = Modifier.fillMaxWidth()
            )

            // Deskripsi
            OutlinedTextField(
                value = viewModel.description.value,
                onValueChange = { viewModel.description.value = it },
                label = { Text("Deskripsi") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Tahun & Kategori
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = viewModel.categoryId.value,
                    onValueChange = { viewModel.categoryId.value = it },
                    label = { Text("ID Kategori") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = viewModel.year.value,
                    onValueChange = { viewModel.year.value = it },
                    label = { Text("Tahun") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Info Tambahan
            Text(
                text = "*File PDF tidak dapat diubah melalui menu ini. Hapus dan upload ulang jika ingin mengganti file.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary
            )

            // Tombol Simpan
            Button(
                onClick = { viewModel.updatePublication() },
                modifier = Modifier.fillMaxWidth(),
                enabled = editState !is Resource.Loading
            ) {
                if (editState is Resource.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menyimpan...")
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan")
                }
            }
        }
    }
}