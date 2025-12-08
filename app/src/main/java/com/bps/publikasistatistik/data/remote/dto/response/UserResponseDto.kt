package com.bps.publikasistatistik.data.remote.dto.response

data class UserResponseDto(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val fullName: String?,
    val gender: String?,
    val placeOfBirth: String?,
    val dateOfBirth: String?,
    val phoneNumber: String?,
    val address: String?,
    val profilePicture: String?, // Nama file gambar
    val createdAt: String?
)