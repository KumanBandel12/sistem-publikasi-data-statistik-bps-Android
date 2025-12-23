package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.request.SubCategoryRequestDto
import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.CategoryResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface CategoryApi {
    @GET("categories")
    suspend fun getAllCategories(): Response<ApiResponseDto<List<CategoryResponseDto>>>

    @GET("categories/{id}/subcategories")
    suspend fun getSubCategories(@Path("id") id: Long): Response<ApiResponseDto<List<CategoryResponseDto>>>

    @GET("categories/tree")
    suspend fun getCategoryTree(): Response<ApiResponseDto<List<CategoryResponseDto>>>

    @POST("categories/{categoryId}/subcategories")
    suspend fun createSubCategory(
        @Path("categoryId") categoryId: Long,
        @Body request: SubCategoryRequestDto
    ): Response<ApiResponseDto<CategoryResponseDto>>

    @PUT("categories/subcategories/{id}")
    suspend fun updateSubCategory(
        @Path("id") id: Long,
        @Body request: SubCategoryRequestDto
    ): Response<ApiResponseDto<CategoryResponseDto>>

    @DELETE("categories/subcategories/{id}")
    suspend fun deleteSubCategory(
        @Path("id") id: Long
    ): Response<ApiResponseDto<Any>>
}