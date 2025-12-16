package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class UserResponseDto(
    @SerializedName("id")
    val id: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("role")
    val role: String,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("placeOfBirth")
    val placeOfBirth: String?,

    @SerializedName("dateOfBirth")
    val dateOfBirth: String?,

    @SerializedName("phoneNumber")
    val phoneNumber: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("profilePicture")
    val profilePicture: String?,
    val createdAt: String?
)
