package com.bps.publikasistatistik.data.mapper

import com.bps.publikasistatistik.data.remote.dto.response.UserResponseDto
import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.util.Constants

fun UserResponseDto.toDomain(): User {
    // Logic URL Profile Picture
    val baseUrl = Constants.BASE_URL.removeSuffix("api/")
    val fullPictureUrl = if (profilePicture != null) {
        "${Constants.BASE_URL}profile/picture?v=$profilePicture"
    } else {
        null
    }

    return User(
        id = id,
        username = username,
        email = email,
        role = role,
        fullName = fullName,
        gender = gender,
        placeOfBirth = placeOfBirth,
        dateOfBirth = dateOfBirth,
        phoneNumber = phoneNumber,
        address = address,
        profilePictureUrl = fullPictureUrl
    )
}