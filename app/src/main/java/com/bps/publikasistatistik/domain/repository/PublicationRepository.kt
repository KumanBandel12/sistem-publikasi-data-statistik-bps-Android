package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
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
    suspend fun getMostDownloaded(limit: Int): Flow<Resource<List<Publication>>>
    suspend fun getSuggestions(keyword: String): Flow<Resource<List<String>>>
    suspend fun deletePublication(id: Long): Flow<Resource<Boolean>>
    suspend fun updatePublication(id: Long, request: PublicationRequestDto): Flow<Resource<Boolean>>
}