package com.bps.publikasistatistik.data.mapper

import com.bps.publikasistatistik.data.remote.dto.response.PublicationResponseDto
import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.util.Constants

fun PublicationResponseDto.toDomain(): Publication {
    val baseUrl = Constants.BASE_URL.removeSuffix("api/")

    // Logic untuk memastikan URL cover lengkap
    val fullCoverUrl = if (coverUrl != null && !coverUrl.startsWith("http")) {
        // Hapus suffix "api/" jika ada, lalu gabung dengan BASE_URL
        val baseUrl = Constants.BASE_URL.removeSuffix("api/")
        // Pastikan tidak ada double slash
        if (coverUrl.startsWith("/")) "$baseUrl${coverUrl.substring(1)}" else "$baseUrl$coverUrl"
    } else {
        coverUrl
    }

    // 2. Logic URL File Download
    val fullFileUrl = if (fileUrl != null && !fileUrl.startsWith("http")) {
        val cleanPath = if (fileUrl.startsWith("/")) fileUrl.substring(1) else fileUrl
        "$baseUrl$cleanPath"
    } else {
        fileUrl
    }

    return Publication(
        id = id,
        title = title,
        description = description,
        coverUrl = fullCoverUrl,
        fileUrl = fullFileUrl,
        year = year,
        views = views,
        downloads = downloads,
        categoryName = category?.name ?: "Umum",
        size = fileSizeFormatted ?: "0 KB"
        // Jika di Domain Model kamu butuh field lain (author, releaseDate), tambahkan di sini
    )
}