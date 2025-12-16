package com.bps.publikasistatistik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestDto(
    @SerializedName("email")
    val email: String
)