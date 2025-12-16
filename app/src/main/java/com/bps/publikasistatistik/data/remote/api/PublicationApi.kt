package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.request.PublicationRequestDto
import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.PublicationResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicationApi {
    @GET("publications") // Endpoint backend: /api/publications
    suspend fun getPublications(
        @Query("search") search: String? = null,
        @Query("categoryId") categoryId: Long? = null,
        @Query("year") year: Int? = null,
        @Query("sort") sort: String? = null,
        @Query("limit") limit: Int? = null
    ): Response<ApiResponseDto<List<PublicationResponseDto>>>

    @GET("publications/{id}")
    suspend fun getPublicationDetail(
        @Path("id") id: Long
    ): Response<ApiResponseDto<PublicationResponseDto>>

    @GET("publications/most-downloaded")
    suspend fun getMostDownloaded(
        @Query("limit") limit: Int = 10 // Default ambil 10 terpopuler
    ): Response<ApiResponseDto<List<PublicationResponseDto>>>

    @GET("publications/suggestions")
    suspend fun getSuggestions(
        @Query("keyword") keyword: String
    ): Response<ApiResponseDto<List<String>>>

    @Multipart
    @POST("publications")
    suspend fun uploadPublication(
        @Part file: MultipartBody.Part,
        @Part("request") request: RequestBody
    ): Response<ApiResponseDto<PublicationResponseDto>>

    @DELETE("publications/{id}")
    suspend fun deletePublication(
        @Path("id") id: Long
    ): Response<ApiResponseDto<Any>>

    // Endpoint Update (Admin Only)
    @PUT("publications/{id}")
    suspend fun updatePublication(
        @Path("id") id: Long,
        @Body request: PublicationRequestDto
    ): Response<ApiResponseDto<PublicationResponseDto>>
}