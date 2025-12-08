package com.bps.publikasistatistik.data.remote.dto.response

data class ApiResponseDto<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)