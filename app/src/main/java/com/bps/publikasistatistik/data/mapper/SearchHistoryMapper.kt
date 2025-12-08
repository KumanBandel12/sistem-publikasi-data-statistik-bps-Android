package com.bps.publikasistatistik.data.mapper

import com.bps.publikasistatistik.data.remote.dto.response.SearchHistoryResponseDto
import com.bps.publikasistatistik.domain.model.SearchHistory

fun SearchHistoryResponseDto.toDomain(): SearchHistory {
    return SearchHistory(
        id = id,
        keyword = keyword,
        searchDate = searchDate ?: ""
    )
}