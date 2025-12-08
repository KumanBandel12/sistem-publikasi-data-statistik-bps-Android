package com.bps.publikasistatistik.presentation.profile.change_password

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "", // Tambahan untuk validasi di UI
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)