package com.bps.publikasistatistik.domain.repository

import com.bps.publikasistatistik.domain.model.Category
import com.bps.publikasistatistik.util.Resource
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategories(): Flow<Resource<List<Category>>>
    suspend fun getSubCategories(parentId: Long): Flow<Resource<List<Category>>>
    suspend fun getCategoryTree(): Flow<Resource<List<Category>>>
}