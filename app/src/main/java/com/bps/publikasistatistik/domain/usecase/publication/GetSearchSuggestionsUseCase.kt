package com.bps.publikasistatistik.domain.usecase.publication

import com.bps.publikasistatistik.domain.repository.PublicationRepository
import javax.inject.Inject

class GetSearchSuggestionsUseCase @Inject constructor(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(keyword: String) = repository.getSuggestions(keyword)
}