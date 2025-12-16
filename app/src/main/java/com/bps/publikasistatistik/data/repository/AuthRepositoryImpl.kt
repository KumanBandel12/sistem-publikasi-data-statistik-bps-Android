package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.local.AuthPreferences // Import ini
import com.bps.publikasistatistik.data.remote.api.AuthApi
import com.bps.publikasistatistik.data.remote.dto.request.ForgotPasswordRequestDto
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
                val data = response.body()?.data
                val token = data?.token
                // Default ke "user" jika null
                val role = data?.user?.role ?: "user"

                if (token != null) {
                    preferences.saveAuthToken(token)
                    preferences.saveUserRole(role) // SIMPAN ROLE
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

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            // 1. Usaha 1: Coba beritahu server (Best Effort)
            // Jika internet mati, baris ini akan melempar error, lalu masuk ke catch
            api.logout()
        } catch (e: Exception) {
            // 2. Jika offline/error server: Biarkan saja (Ignore).
            // Kita tidak ingin user gagal logout hanya karena sinyal jelek.
        } finally {
            // 3. Usaha 2 (WAJIB): Hapus token di HP
            // Blok finally SELALU dieksekusi, sukses ataupun error
            preferences.clearAuthToken()

            // 4. Kabari UI bahwa logout berhasil (User aman keluar)
            emit(Resource.Success(true))
        }
    }

    override suspend fun forgotPassword(
        email: String,
        dateOfBirth: String,
        placeOfBirth: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = ForgotPasswordRequestDto(
                email = email,
                dateOfBirth = dateOfBirth,
                placeOfBirth = placeOfBirth,
                newPassword = newPassword,
                confirmPassword = confirmPassword
            )
            val response = api.forgotPassword(request)

            if (response.isSuccessful && response.body()?.success == true) {
                // Pesan sukses dari backend (misal: "Link reset telah dikirim ke email")
                emit(Resource.Success(response.body()?.message ?: "Cek email Anda untuk reset password"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memproses permintaan"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}