package com.bps.publikasistatistik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class RegisterRequestDto(
    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String
)