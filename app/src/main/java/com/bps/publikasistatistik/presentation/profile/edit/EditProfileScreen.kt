package com.bps.publikasistatistik.presentation.profile.edit

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import com.bps.publikasistatistik.util.UriUtils
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.background
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    // Handle Sukses/Error
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()
            navController.popBackStack() // Kembali ke halaman profil
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                // Konversi URI ke File lalu Upload
                val file = UriUtils.getFileFromUri(context, uri)
                if (file != null) {
                    viewModel.uploadPicture(file)
                } else {
                    Toast.makeText(context, "Gagal memproses gambar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
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
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()) // Agar bisa discroll
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .clickable {
                            // Buka Galeri saat diklik
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                ) {
                    AsyncImage(
                        model = state.profilePictureUrl, // URL dari state
                        contentDescription = "Foto Profil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(android.R.drawable.ic_menu_camera) // Placeholder jika error/null
                    )

                    // Icon Edit kecil overlay
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .background(Color.White, CircleShape)
                            .padding(4.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (!state.profilePictureUrl.isNullOrEmpty()) {
                TextButton(
                    onClick = { viewModel.deleteProfilePicture() },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    enabled = !state.isLoading
                ) {
                    Text(
                        text = "Hapus Foto",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Nama Lengkap
            OutlinedTextField(
                value = state.fullName,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredFullName(it)) },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            OutlinedTextField(
                value = state.username,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredUsername(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email (Read Only sebaiknya, atau boleh diedit)
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredEmail(it)) },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // No HP
            OutlinedTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredPhone(it)) },
                label = { Text("Nomor HP (08xx / 628xx)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tempat Lahir
            OutlinedTextField(
                value = state.placeOfBirth,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredPlaceOfBirth(it)) },
                label = { Text("Tempat Lahir") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tanggal Lahir (Sementara text manual yyyy-mm-dd)
            // TODO: Nanti bisa diganti DatePicker Dialog
            OutlinedTextField(
                value = state.dateOfBirth,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredDateOfBirth(it)) },
                label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                placeholder = { Text("Contoh: 1990-12-31") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Alamat
            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(EditProfileEvent.EnteredAddress(it)) },
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3 // Kotak lebih besar
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Simpan
            Button(
                onClick = { viewModel.saveProfile() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simpan Perubahan")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = { showDeleteDialog = true }, // Munculkan dialog
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error // Warna Merah
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Hapus Akun Permanen")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // --- DIALOG KONFIRMASI ---
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Akun?") },
                text = {
                    Text("Tindakan ini tidak dapat dibatalkan. Semua data profil dan riwayat Anda akan dihapus permanen.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteAccount() // Panggil fungsi hapus
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Ya, Hapus")
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
}