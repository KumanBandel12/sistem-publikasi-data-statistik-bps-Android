package com.bps.publikasistatistik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class ForgotPasswordRequestDto(
    @SerializedName("email")
    val email: String,
    
    @SerializedName("dateOfBirth")
    val dateOfBirth: String,  // Format: yyyy-MM-dd
    
    @SerializedName("placeOfBirth")
    val placeOfBirth: String,
    
    @SerializedName("newPassword")
    val newPassword: String,
    
    @SerializedName("confirmPassword")
    val confirmPassword: String
)