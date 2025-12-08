package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.NotificationApi
import com.bps.publikasistatistik.domain.repository.NotificationRepository
import com.bps.publikasistatistik.util.Resource
import com.bps.publikasistatistik.domain.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val api: NotificationApi
) : NotificationRepository {

    override suspend fun getNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getNotifications()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat notifikasi"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun markAsRead(id: Long): Flow<Resource<Boolean>> = flow {
        try {
            val response = api.markAsRead(id)
            if (response.isSuccessful) emit(Resource.Success(true))
            else emit(Resource.Error("Gagal update status"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }

    override suspend fun markAllAsRead(): Flow<Resource<Boolean>> = flow {
        try {
            val response = api.markAllAsRead()
            if (response.isSuccessful) emit(Resource.Success(true))
            else emit(Resource.Error("Gagal update status"))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Error"))
        }
    }
}