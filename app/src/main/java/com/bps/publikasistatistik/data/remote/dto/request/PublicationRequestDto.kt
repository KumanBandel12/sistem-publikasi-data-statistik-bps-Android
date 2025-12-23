package com.bps.publikasistatistik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class PublicationRequestDto(
    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("categoryId")
    val categoryId: Long,

    @SerializedName("subCategoryId")
    val subCategoryId: Long? = null,

    @SerializedName("year")
    val year: Int,

    // Field tambahan sesuai openapi.yaml (komponen schemas: PublicationRequest)
    @SerializedName("author")
    val author: String? = "Admin",

    @SerializedName("catalogNumber")
    val catalogNumber: String? = null,

    @SerializedName("publicationNumber")
    val publicationNumber: String? = null,

    @SerializedName("issnIsbn")
    val issnIsbn: String? = null,

    @SerializedName("releaseFrequency")
    val releaseFrequency: String? = null,

    @SerializedName("releaseDate")
    val releaseDate: String? = null, // Format: YYYY-MM-DD

    @SerializedName("language")
    val language: String? = "Indonesia",

    @SerializedName("isFeatured")
    val isFeatured: Boolean? = false
)