package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.local.AuthPreferences // Import ini
import com.bps.publikasistatistik.data.remote.api.AuthApi
import com.bps.publikasistatistik.data.remote.dto.request.LoginRequestDto
import com.bps.publikasistatistik.data.remote.dto.request.RegisterRequestDto
import com.bps.publikasistatistik.domain.repository.AuthRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val preferences: AuthPreferences // Inject Preferences ke sini
) : AuthRepository {

    override suspend fun register(username: String, email: String, pass: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = RegisterRequestDto(username, email, pass)
            val response = api.register(request)

            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success("Registrasi berhasil, silakan login"))
            } else {
                // Ambil pesan error dari backend (misal: "Username already taken")
                emit(Resource.Error(response.body()?.message ?: "Registrasi gagal"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun login(email: String, pass: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.login(LoginRequestDto(email, pass))

            if (response.isSuccessful && response.body()?.success == true) {
                val token = response.body()?.data?.token
                if (token != null) {
                    preferences.saveAuthToken(token)
                    emit(Resource.Success(token))
                } else {
                    emit(Resource.Error("Token kosong dari server"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Login Gagal"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Terjadi kesalahan: ${e.localizedMessage}"))
        }
    }

    override suspend fun logout() {
        preferences.clearAuthToken()
    }
}