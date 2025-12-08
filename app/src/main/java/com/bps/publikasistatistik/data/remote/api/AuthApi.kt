package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.request.LoginRequestDto
import com.bps.publikasistatistik.data.remote.dto.request.RegisterRequestDto
import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.AuthResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.UserResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponseDto<AuthResponseDto>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequestDto): Response<ApiResponseDto<UserResponseDto>>
}