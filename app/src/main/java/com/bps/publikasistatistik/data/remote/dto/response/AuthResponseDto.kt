package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("token")
    val token: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("user")
    val user: UserResponseDto
)