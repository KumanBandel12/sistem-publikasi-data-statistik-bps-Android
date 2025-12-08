package com.bps.publikasistatistik.presentation.profile

import com.bps.publikasistatistik.domain.model.User

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedOut: Boolean = false // Penanda jika user berhasil logout
)