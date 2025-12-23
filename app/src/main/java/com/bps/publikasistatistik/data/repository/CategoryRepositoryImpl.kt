package com.bps.publikasistatistik.data.repository

import com.bps.publikasistatistik.data.mapper.toDomain
import com.bps.publikasistatistik.data.remote.api.CategoryApi
import com.bps.publikasistatistik.data.remote.dto.request.SubCategoryRequestDto
import com.bps.publikasistatistik.domain.repository.CategoryRepository
import com.bps.publikasistatistik.util.Resource
import com.bps.publikasistatistik.domain.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val api: CategoryApi
) : CategoryRepository {

    override suspend fun getCategories(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getAllCategories()
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getSubCategories(parentId: Long): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getSubCategories(parentId)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat sub-kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun getCategoryTree(): Flow<Resource<List<Category>>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getCategoryTree()
            if (response.isSuccessful && response.body()?.success == true) {
                // Mapper akan otomatis menangani struktur nested/tree
                val data = response.body()?.data?.map { it.toDomain() } ?: emptyList()
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal memuat struktur kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun createSubCategory(
        categoryId: Long,
        name: String,
        description: String?
    ): Flow<Resource<Category>> = flow {
        emit(Resource.Loading())
        try {
            val request = SubCategoryRequestDto(name, description)
            val response = api.createSubCategory(categoryId, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.toDomain()
                if (data != null) {
                    emit(Resource.Success(data))
                } else {
                    emit(Resource.Error("Data sub-kategori kosong"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal membuat sub-kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun updateSubCategory(
        id: Long,
        name: String,
        description: String?
    ): Flow<Resource<Category>> = flow {
        emit(Resource.Loading())
        try {
            val request = SubCategoryRequestDto(name, description)
            val response = api.updateSubCategory(id, request)
            if (response.isSuccessful && response.body()?.success == true) {
                val data = response.body()?.data?.toDomain()
                if (data != null) {
                    emit(Resource.Success(data))
                } else {
                    emit(Resource.Error("Data sub-kategori kosong"))
                }
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal mengupdate sub-kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }

    override suspend fun deleteSubCategory(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.deleteSubCategory(id)
            if (response.isSuccessful && response.body()?.success == true) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error(response.body()?.message ?: "Gagal menghapus sub-kategori"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Error: ${e.localizedMessage}"))
        }
    }
}