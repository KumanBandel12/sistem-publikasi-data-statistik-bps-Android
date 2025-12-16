package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.UserApi
import com.bps.publikasistatistik.data.remote.dto.request.ChangePasswordRequestDto
import com.bps.publikasistatistik.data.remote.dto.request.UpdateProfileRequestDto
import com.bps.publikasistatistik.domain.model.User
import com.bps.publikasistatistik.domain.repository.UserRepository
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

class UserRepositoryImpl @Inject constructor(
    private val api: UserApi
    ) : UserRepository {

    override suspend fun getProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    emit(Resource.Success(data.toDomain()))
                } else {
                    emit(Resource.Error("Data profil kosong"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat profil"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateProfile(user: User): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val request = UpdateProfileRequestDto(
                username = user.username,
                email = user.email,
                fullName = user.fullName,
                gender = user.gender,
                placeOfBirth = user.placeOfBirth,
                dateOfBirth = user.dateOfBirth,
                phoneNumber = user.phoneNumber,
                address = user.address
            )
            val response = api.updateProfile(request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    emit(Resource.Success(data.toDomain()))
                } else {
                    emit(Resource.Error("Gagal update profil"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal update profil"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun changePassword(oldPass: String, newPass: String): Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val request = ChangePasswordRequestDto(oldPass, newPass)
            val response = api.changePassword(request)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success("Password berhasil diubah"))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal ubah password"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun uploadProfilePicture(file: java.io.File): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            // Siapkan RequestBody (image/*)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

            // Siapkan MultipartBody.Part (nama parameter harus "file" sesuai backend @RequestParam("file"))
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = api.uploadProfilePicture(body)

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    emit(Resource.Success(data.toDomain()))
                } else {
                    emit(Resource.Error("Gagal upload gambar: Data kosong"))
                }
            } else {
                // Coba baca pesan error asli dari server
                val errorBody = response.errorBody()?.string()

                // Parse pesan error (Backend Anda mengembalikan JSON ApiResponse saat error)
                val errorMessage = try {
                    // Cara sederhana parsing JSON string untuk mencari field "message"
                    if (errorBody != null && errorBody.contains("message")) {
                        val json = org.json.JSONObject(errorBody)
                        json.getString("message")
                    } else {
                        "Gagal upload: Kode ${response.code()}"
                    }
                } catch (e: Exception) {
                    "Gagal upload gambar (Error parsing)"
                }

                emit(Resource.Error(errorMessage))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteAccount(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteAccount()
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal menghapus akun"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteProfilePicture(): Flow<Resource<User>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteProfilePicture()

            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data
                if (data != null) {
                    emit(Resource.Success(data.toDomain()))
                } else {
                    emit(Resource.Error("Gagal menghapus foto"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal menghapus foto"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}