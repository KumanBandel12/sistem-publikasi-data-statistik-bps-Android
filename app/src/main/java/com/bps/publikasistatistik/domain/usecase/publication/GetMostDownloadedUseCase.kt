package com.bps.publikasistatistik.domain.usecase.publication

import com.bps.publikasistatistik.domain.repository.PublicationRepository
import javax.inject.Inject

class GetMostDownloadedUseCase @Inject constructor(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(limit: Int = 5) = repository.getMostDownloaded(limit)
}