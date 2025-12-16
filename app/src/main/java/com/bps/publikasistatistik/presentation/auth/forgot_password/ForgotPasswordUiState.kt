package com.bps.publikasistatistik.presentation.auth.forgot_password

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)