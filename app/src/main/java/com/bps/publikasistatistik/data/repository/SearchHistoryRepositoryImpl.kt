package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.SearchHistoryApi
import com.bps.publikasistatistik.domain.repository.SearchHistoryRepository
import com.bps.publikasistatistik.util.Resource
import com.bps.publikasistatistik.domain.model.SearchHistory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SearchHistoryRepositoryImpl @Inject constructor(
    private val api: SearchHistoryApi
) : SearchHistoryRepository {

    override suspend fun getSearchHistory(): Flow<Resource<List<SearchHistory>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getSearchHistory()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat riwayat"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun clearSearchHistory(): Flow<Resource<Boolean>> = flow {
        try {
            val response = api.clearSearchHistory()
            if (response.isSuccessful) emit(Resource.Success(true))
            else emit(Resource.Error("Gagal menghapus riwayat"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}