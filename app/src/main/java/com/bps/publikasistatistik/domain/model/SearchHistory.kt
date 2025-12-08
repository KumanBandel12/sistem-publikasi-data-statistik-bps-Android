package com.bps.publikasistatistik.domain.model

data class SearchHistory(
    val id: Long,
    val keyword: String,
    val searchDate: String
)