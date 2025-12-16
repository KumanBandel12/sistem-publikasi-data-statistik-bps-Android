package com.bps.publikasistatistik.presentation.auth.login

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bps.publikasistatistik.presentation.theme.BpsPrimary

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    onNavigateToForgotPassword: () -> Unit = {}
) {
    val state = viewModel.state.value
    val context = LocalContext.current

    // --- SKENARIO 3: KEYBOARD MANAGER ---
    val focusManager = LocalFocusManager.current

    // State Lokal untuk UI (Visibility & Validasi Error)
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            Toast.makeText(context, "Login Berhasil!", Toast.LENGTH_SHORT).show()
            onLoginSuccess()
        }
    }

    LaunchedEffect(state.error) {
        if (state.error != null) {
            Toast.makeText(context, state.error, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Judul lebih besar dan berwarna
        Text(
            text = "Masuk",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = BpsPrimary
        )
        Text(
            text = "Silakan login untuk melanjutkan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- EMAIL INPUT ---
        OutlinedTextField(
            value = viewModel.email.value,
            onValueChange = {
                viewModel.onEmailChange(it)
                emailError = null // Reset error saat mengetik
            },
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = emailError != null,
            supportingText = {
                if (emailError != null) {
                    Text(emailError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- PASSWORD INPUT ---
        OutlinedTextField(
            value = viewModel.password.value,
            onValueChange = {
                viewModel.onPasswordChange(it)
                passwordError = null
            },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus() // Tutup keyboard saat Enter
                }
            ),
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) {
                    Text(passwordError!!, color = MaterialTheme.colorScheme.error)
                }
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- TOMBOL LOGIN ---
        Button(
            onClick = {
                // 1. TUTUP KEYBOARD
                focusManager.clearFocus()

                // 2. VALIDASI INPUT (Skenario 2)
                var isValid = true
                val emailVal = viewModel.email.value
                val passVal = viewModel.password.value

                // Cek Email
                if (emailVal.isBlank()) {
                    emailError = "Email wajib diisi"
                    isValid = false
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailVal).matches()) {
                    emailError = "Format email tidak valid"
                    isValid = false
                }

                // Cek Password
                if (passVal.isBlank()) {
                    passwordError = "Password wajib diisi"
                    isValid = false
                }

                // 3. JIKA VALID, PANGGIL VIEWMODEL
                if (isValid) {
                    viewModel.login()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !state.isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = BpsPrimary)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Memproses...")
            } else {
                Text("LOGIN", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- LUPA PASSWORD ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "Lupa Password?",
                color = BpsPrimary,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable {
                    onNavigateToForgotPassword()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- FOOTER DAFTAR ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Belum punya akun? ")
            Text(
                text = "Daftar Sekarang",
                color = BpsPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onNavigateToRegister()
                }
            )
        }
    }
}