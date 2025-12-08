package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.Publication
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface PublicationRepository {
    suspend fun getPublications(
        search: String? = null,
        categoryId: Long? = null,
        year: Int? = null,
        sort: String? = null
    ): Flow<Resource<List<Publication>>>

    suspend fun getPublicationDetail(id: Long): Flow<Resource<Publication>>
}