package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.CategoryResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface CategoryApi {
    @GET("categories")
    suspend fun getAllCategories(): Response<ApiResponseDto<List<CategoryResponseDto>>>
}