package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getProfile(): Flow<Resource<User>>
    suspend fun updateProfile(user: User): Flow<Resource<User>> // Kirim objek User, nanti dimapping ke DTO di impl
    suspend fun changePassword(oldPass: String, newPass: String): Flow<Resource<String>>

    suspend fun uploadProfilePicture(file: java.io.File): Flow<Resource<User>>
}