package com.bps.publikasistatistik.data.mapper

import com.bps.publikasistatistik.data.remote.dto.response.CategoryResponseDto
import com.bps.publikasistatistik.domain.model.Category

fun CategoryResponseDto.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        description = description,
        totalPublications = publicationCount ?: 0L
    )
}