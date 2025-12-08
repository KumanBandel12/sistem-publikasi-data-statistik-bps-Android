package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.SearchHistory
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    suspend fun getSearchHistory(): Flow<Resource<List<SearchHistory>>>
    suspend fun clearSearchHistory(): Flow<Resource<Boolean>>
}