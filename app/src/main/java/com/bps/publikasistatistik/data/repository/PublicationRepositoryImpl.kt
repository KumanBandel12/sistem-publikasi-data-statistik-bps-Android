package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.PublicationApi
import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
import com.bps.publikasistatistik.domain.repository.PublicationRepository
import com.bps.publikasistatistik.util.Resource
import com.bps.publikasistatistik.domain.model.Publication
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PublicationRepositoryImpl @Inject constructor(
    private val api: PublicationApi
) : PublicationRepository {

    override suspend fun getPublications(
        search: String?,
        categoryId: Long?,
        year: Int?,
        sort: String?
    ): Flow<Resource<List<Publication>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPublications(search, categoryId, year, sort)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat data"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error jaringan: ${e.localizedMessage}"))
        }
    }

    override suspend fun getPublicationDetail(id: Long): Flow<Resource<Publication>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPublicationDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    emit(Resource.Success(data.toDomain()))
                } else {
                    emit(Resource.Error("Data publikasi tidak ditemukan"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat detail"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error jaringan: ${e.localizedMessage}"))
        }
    }

    override suspend fun getMostDownloaded(limit: Int): Flow<Resource<List<Publication>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getMostDownloaded(limit)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat data populer"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getSuggestions(keyword: String): Flow<Resource<List<String>>> = flow {
        // Tidak perlu emit Loading agar UI tidak berkedip heboh saat mengetik
        try {
            val response = api.getSuggestions(keyword)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat saran"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun deletePublication(id: Long): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deletePublication(id)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal menghapus publikasi"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun updatePublication(id: Long, request: PublicationRequestDto): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.updatePublication(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal mengupdate publikasi"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}