package com.bps.publikasistatistik.presentation.profile.change_password

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    navController: NavController,
    viewModel: ChangePasswordViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    // Handle Sukses
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    // Handle Error
    LaunchedEffect(state.error) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ubah Password") },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Text(
                text = "Buat password baru yang kuat untuk mengamankan akun Anda.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 1. Password Lama
            OutlinedTextField(
                value = state.oldPassword,
                onValueChange = { viewModel.onEvent(ChangePasswordEvent.EnteredOldPassword(it)) },
                label = { Text("Password Lama") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Password Baru
            OutlinedTextField(
                value = state.newPassword,
                onValueChange = { viewModel.onEvent(ChangePasswordEvent.EnteredNewPassword(it)) },
                label = { Text("Password Baru") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                supportingText = { Text("Minimal 8 karakter, ada huruf besar & angka") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Konfirmasi Password
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = { viewModel.onEvent(ChangePasswordEvent.EnteredConfirmPassword(it)) },
                label = { Text("Konfirmasi Password Baru") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                isError = state.newPassword.isNotEmpty() && state.confirmPassword.isNotEmpty() && state.newPassword != state.confirmPassword
            )

            if (state.newPassword.isNotEmpty() && state.confirmPassword.isNotEmpty() && state.newPassword != state.confirmPassword) {
                Text(
                    text = "Password tidak cocok",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Tombol Simpan
            Button(
                onClick = { viewModel.changePassword() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !state.isLoading &&
                        state.oldPassword.isNotEmpty() &&
                        state.newPassword.isNotEmpty() &&
                        state.newPassword == state.confirmPassword
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Ubah Password")
                }
            }
        }
    }
}