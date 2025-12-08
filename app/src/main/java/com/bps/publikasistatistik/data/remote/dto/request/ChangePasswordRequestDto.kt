package com.bps.publikasistatistik.data.remote.dto.request

data class ChangePasswordRequestDto(
    val oldPassword: String,
    val newPassword: String
)