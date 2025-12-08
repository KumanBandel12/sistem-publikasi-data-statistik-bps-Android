package com.bps.publikasistatistik.domain.model

data class User(
    val id: Long,
    val username: String,
    val email: String,
    val role: String,
    val fullName: String?,
    val gender: String?,
    val placeOfBirth: String?,
    val dateOfBirth: String?, // String yyyy-MM-dd
    val phoneNumber: String?,
    val address: String?,
    val profilePictureUrl: String?
)