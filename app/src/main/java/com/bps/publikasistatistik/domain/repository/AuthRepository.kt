package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(username: String, email: String, pass: String): Flow<Resource<String>>
    suspend fun login(email: String, pass: String): Flow<Resource<String>>
    suspend fun logout()
}