package com.bps.publikasistatistik.presentation.auth.forgot_password

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lupa Password") },
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
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (state.isSuccess) {
                // Tampilan Sukses
                Spacer(modifier = Modifier.height(48.dp))
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Reset Password Berhasil!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.successMessage ?: "Password Anda telah berhasil direset.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Kembali ke Login")
                }
            } else {
                // Tampilan Form
                Text(
                    text = "Reset Password",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Masukkan informasi akun Anda untuk mereset password.",
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.email,
                    onValueChange = viewModel::onEmailChange,
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = state.error != null && state.email.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.dateOfBirth,
                    onValueChange = viewModel::onDateOfBirthChange,
                    label = { Text("Tanggal Lahir (YYYY-MM-DD)") },
                    placeholder = { Text("Contoh: 1990-12-31") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.error != null && state.dateOfBirth.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.placeOfBirth,
                    onValueChange = viewModel::onPlaceOfBirthChange,
                    label = { Text("Tempat Lahir") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = state.error != null && state.placeOfBirth.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.newPassword,
                    onValueChange = viewModel::onNewPasswordChange,
                    label = { Text("Password Baru") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Sembunyikan password" else "Tampilkan password"
                            )
                        }
                    },
                    isError = state.error != null && state.newPassword.isBlank()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    label = { Text("Konfirmasi Password Baru") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Sembunyikan password" else "Tampilkan password"
                            )
                        }
                    },
                    isError = state.error != null && state.confirmPassword.isBlank()
                )

                if (state.error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.Start)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.submit() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Reset Password")
                    }
                }
            }
        }
    }
}