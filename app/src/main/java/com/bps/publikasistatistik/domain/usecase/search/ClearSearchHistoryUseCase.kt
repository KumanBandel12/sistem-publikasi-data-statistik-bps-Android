package com.bps.publikasistatistik.domain.usecase.search

import com.bps.publikasistatistik.domain.repository.SearchHistoryRepository
import javax.inject.Inject

class ClearSearchHistoryUseCase @Inject constructor(
    private val repository: SearchHistoryRepository
) {
    suspend operator fun invoke() = repository.clearSearchHistory()
}