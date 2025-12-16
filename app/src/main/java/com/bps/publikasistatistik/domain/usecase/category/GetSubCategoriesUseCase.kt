package com.bps.publikasistatistik.domain.usecase.category

import com.bps.publikasistatistik.domain.repository.CategoryRepository
import javax.inject.Inject

class GetSubCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(parentId: Long) = repository.getSubCategories(parentId)
}