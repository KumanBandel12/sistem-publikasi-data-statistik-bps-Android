package com.bps.publikasistatistik.presentation.profile.edit

data class EditProfileUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,

    val profilePictureUrl: String? = null,

    // Field Formulir
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val gender: String = "Laki-laki", // Default
    val placeOfBirth: String = "",
    val dateOfBirth: String = "" // Format yyyy-MM-dd
)