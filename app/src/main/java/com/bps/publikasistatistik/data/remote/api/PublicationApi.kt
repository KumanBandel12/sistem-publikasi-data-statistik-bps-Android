package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.PublicationResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicationApi {
    @GET("publications") // Endpoint backend: /api/publications
    suspend fun getPublications(
        @Query("search") search: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("year") year: Int? = null,
        @Query("sort") sort: String? = "latest"
    ): Response<ApiResponseDto<List<PublicationResponseDto>>>

    @GET("publications/{id}")
    suspend fun getPublicationDetail(
        @Path("id") id: Long
    ): Response<ApiResponseDto<PublicationResponseDto>>
}