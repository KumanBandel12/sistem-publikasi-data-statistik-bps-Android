package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.CategoryResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {
    @GET("categories")
    suspend fun getAllCategories(): Response<ApiResponseDto<List<CategoryResponseDto>>>

    @GET("categories/{id}/subcategories")
    suspend fun getSubCategories(@Path("id") id: Long): Response<ApiResponseDto<List<CategoryResponseDto>>>

    @GET("categories/tree")
    suspend fun getCategoryTree(): Response<ApiResponseDto<List<CategoryResponseDto>>>
}