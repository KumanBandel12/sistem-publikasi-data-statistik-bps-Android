package com.bps.publikasistatistik.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class SubCategoryRequestDto(
    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String? = null
)
