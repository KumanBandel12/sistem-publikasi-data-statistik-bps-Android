package com.bps.publikasistatistik.data.remote.dto.response

data class AuthResponseDto(
    val token: String,
    val type: String,
    // Kita abaikan user detail dulu agar simpel
)