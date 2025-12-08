package com.bps.publikasistatistik.data.remote.api

import com.bps.publikasistatistik.data.remote.dto.response.ApiResponseDto
import com.bps.publikasistatistik.data.remote.dto.response.SearchHistoryResponseDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET

interface SearchHistoryApi {
    @GET("search/history")
    suspend fun getSearchHistory(): Response<ApiResponseDto<List<SearchHistoryResponseDto>>>

    @DELETE("search/history")
    suspend fun clearSearchHistory(): Response<ApiResponseDto<Void>>
}