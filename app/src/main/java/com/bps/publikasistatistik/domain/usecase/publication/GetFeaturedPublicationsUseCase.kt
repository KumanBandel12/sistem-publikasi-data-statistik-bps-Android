package com.bps.publikasistatistik.domain.usecase.publication

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.domain.repository.PublicationRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeaturedPublicationsUseCase @Inject constructor(
    private val repository: PublicationRepository
) {
    suspend operator fun invoke(): Flow<Resource<List<Publication>>> {
        return repository.getFeaturedPublications()
    }
}
