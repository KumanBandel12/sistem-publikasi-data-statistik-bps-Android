package com.bps.publikasistatistik.data.remote.dto.request

data class UpdateProfileRequestDto(
    val username: String,
    val email: String,
    val fullName: String?,
    val gender: String?,
    val placeOfBirth: String?,
    val dateOfBirth: String?, // Format: yyyy-MM-dd
    val phoneNumber: String?,
    val address: String?
)