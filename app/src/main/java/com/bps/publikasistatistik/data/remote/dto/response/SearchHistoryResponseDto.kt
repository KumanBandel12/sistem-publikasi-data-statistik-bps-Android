package com.bps.publikasistatistik.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class SearchHistoryResponseDto(
    val id: Long,
    val keyword: String,
    val searchDate: String?
)