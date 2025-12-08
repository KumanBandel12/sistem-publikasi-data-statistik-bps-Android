package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class PublicationResponseDto(
    val id: Long,
    val title: String,
    val description: String?,
    val catalogNumber: String?,
    val publicationNumber: String?,
    val issnIsbn: String?,
    val releaseFrequency: String?,
    val releaseDate: String?, // Terima sebagai String (yyyy-MM-dd)
    val language: String?,
    val coverUrl: String?,
    val fileUrl: String?,
    val fileSizeFormatted: String?,
    val year: Int,
    val author: String?,
    val views: Int,
    val downloads: Int,
    val category: CategoryDto?,
    val uploadedBy: UserDto?, // Tambahkan ini jika butuh info uploader
    val createdAt: String?,
    val updatedAt: String?
)

data class CategoryDto(
    val id: Long,
    val name: String,
    val description: String?
)

data class UserDto(
    val id: Long,
    val username: String,
    val email: String
)