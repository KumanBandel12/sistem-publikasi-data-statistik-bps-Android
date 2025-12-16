package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.request.ChangePasswordRequestDto
import com.bps.publikasistatistik.data.remote.dto.request.UpdateProfileRequestDto
import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.UserResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UserApi {
    @GET("profile")
    suspend fun getProfile(): Response<ApiResponseDto<UserResponseDto>>

    @PUT("profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): Response<ApiResponseDto<UserResponseDto>>

    @PUT("profile/password")
    suspend fun changePassword(@Body request: ChangePasswordRequestDto): Response<ApiResponseDto<Void>>

    @Multipart
    @POST("profile/picture")
    suspend fun uploadProfilePicture(@Part file: MultipartBody.Part): Response<ApiResponseDto<UserResponseDto>>

    @DELETE("profile")
    suspend fun deleteAccount(): Response<ApiResponseDto<Void>>

    @DELETE("profile/picture")
    suspend fun deleteProfilePicture(): Response<ApiResponseDto<UserResponseDto>>
}